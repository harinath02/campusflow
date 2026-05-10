import fs from "node:fs/promises";

const {
  GITHUB_TOKEN,
  OPENAI_API_KEY,
  GITHUB_REPOSITORY,
  GITHUB_EVENT_PATH,
  GITHUB_SERVER_URL,
  GITHUB_RUN_ID,
} = process.env;

if (!GITHUB_TOKEN) {
  throw new Error("GITHUB_TOKEN is missing.");
}

if (!OPENAI_API_KEY) {
  console.log("OPENAI_API_KEY is not configured. Skipping AI PR review.");
  process.exit(0);
}

const event = JSON.parse(await fs.readFile(GITHUB_EVENT_PATH, "utf8"));

if (!event.pull_request) {
  console.log("This workflow is not running for a pull request. Skipping.");
  process.exit(0);
}

const [owner, repo] = GITHUB_REPOSITORY.split("/");
const pr = event.pull_request;
const prNumber = pr.number;

const githubApi = "https://api.github.com";
const githubHeaders = {
  Authorization: `Bearer ${GITHUB_TOKEN}`,
  Accept: "application/vnd.github+json",
  "X-GitHub-Api-Version": "2022-11-28",
};

async function githubFetch(path, options = {}) {
  const response = await fetch(`${githubApi}${path}`, {
    ...options,
    headers: {
      ...githubHeaders,
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`GitHub API failed: ${response.status} ${response.statusText}\n${text}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

async function getTextFile(path) {
  try {
    const response = await fetch(
      `${githubApi}/repos/${owner}/${repo}/contents/${encodeURIComponent(path)}?ref=${pr.base.ref}`,
      {
        headers: githubHeaders,
      }
    );

    if (!response.ok) {
      return "";
    }

    const data = await response.json();
    if (!data.content) {
      return "";
    }

    return Buffer.from(data.content, "base64").toString("utf8");
  } catch {
    return "";
  }
}

async function getPullRequestDiff() {
  const response = await fetch(`${githubApi}/repos/${owner}/${repo}/pulls/${prNumber}`, {
    headers: {
      ...githubHeaders,
      Accept: "application/vnd.github.v3.diff",
    },
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`Failed to fetch PR diff: ${response.status}\n${text}`);
  }

  return response.text();
}

async function getChangedFiles() {
  const files = [];
  let page = 1;

  while (true) {
    const batch = await githubFetch(
      `/repos/${owner}/${repo}/pulls/${prNumber}/files?per_page=100&page=${page}`
    );

    files.push(...batch);

    if (batch.length < 100) break;
    page++;
  }

  return files;
}

async function getRecentMergedPrMemory() {
  const query = `repo:${owner}/${repo} is:pr is:merged sort:updated-desc`;
  const result = await githubFetch(
    `/search/issues?q=${encodeURIComponent(query)}&per_page=5`
  );

  const memory = [];

  for (const item of result.items || []) {
    try {
      const comments = await githubFetch(
        `/repos/${owner}/${repo}/issues/${item.number}/comments?per_page=10`
      );

      const commentSummary = comments
        .map((comment) => `- ${comment.user?.login || "unknown"}: ${comment.body || ""}`)
        .join("\n")
        .slice(0, 4000);

      memory.push(
        `PR #${item.number}: ${item.title}\nURL: ${item.html_url}\nRecent comments:\n${commentSummary}`
      );
    } catch {
      memory.push(`PR #${item.number}: ${item.title}\nURL: ${item.html_url}`);
    }
  }

  return memory.join("\n\n").slice(0, 10000);
}

async function getCurrentPrComments() {
  const comments = await githubFetch(
    `/repos/${owner}/${repo}/issues/${prNumber}/comments?per_page=50`
  );

  return comments
    .filter((comment) => !comment.body?.includes("<!-- ai-pr-reviewer -->"))
    .map((comment) => `- ${comment.user?.login || "unknown"}: ${comment.body || ""}`)
    .join("\n")
    .slice(0, 8000);
}

function trimLargeInput(value, maxChars) {
  if (!value) return "";
  if (value.length <= maxChars) return value;

  return `${value.slice(0, maxChars)}

[TRUNCATED: input was too large. Review may be incomplete.]`;
}

function buildFileSummary(files) {
  return files
    .map((file) => {
      return [
        `File: ${file.filename}`,
        `Status: ${file.status}`,
        `Additions: ${file.additions}`,
        `Deletions: ${file.deletions}`,
        `Changes: ${file.changes}`,
        file.patch ? `Patch:\n${trimLargeInput(file.patch, 5000)}` : "Patch: not available",
      ].join("\n");
    })
    .join("\n\n");
}

async function callOpenAI(prompt) {
  const response = await fetch("https://api.openai.com/v1/responses", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${OPENAI_API_KEY}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: "gpt-5.1",
      input: prompt,
      max_output_tokens: 5000,
    }),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`OpenAI API failed: ${response.status} ${response.statusText}\n${text}`);
  }

  const data = await response.json();

  if (data.output_text) {
    return data.output_text;
  }

  const parts = [];

  for (const item of data.output || []) {
    for (const content of item.content || []) {
      if (content.text) parts.push(content.text);
    }
  }

  return parts.join("\n").trim();
}

async function findExistingBotComment() {
  const comments = await githubFetch(
    `/repos/${owner}/${repo}/issues/${prNumber}/comments?per_page=100`
  );

  return comments.find((comment) =>
    comment.user?.type === "Bot" &&
    comment.body?.includes("<!-- ai-pr-reviewer -->")
  );
}

async function upsertPrComment(body) {
  const existing = await findExistingBotComment();

  if (existing) {
    await githubFetch(`/repos/${owner}/${repo}/issues/comments/${existing.id}`, {
      method: "PATCH",
      body: JSON.stringify({ body }),
      headers: {
        "Content-Type": "application/json",
      },
    });
    return;
  }

  await githubFetch(`/repos/${owner}/${repo}/issues/${prNumber}/comments`, {
    method: "POST",
    body: JSON.stringify({ body }),
    headers: {
      "Content-Type": "application/json",
    },
  });
}

const guidelines = await getTextFile(".github/review-guidelines.md");
const readme = await getTextFile("README.md");
const diff = await getPullRequestDiff();
const files = await getChangedFiles();
const recentMemory = await getRecentMergedPrMemory();
const currentComments = await getCurrentPrComments();

const fileSummary = buildFileSummary(files);

const prompt = `
You are the CampusFlow AI PR reviewer.

Follow these review guidelines strictly:

${guidelines}

Repository README context:
${trimLargeInput(readme, 8000)}

Current Pull Request:
- PR Number: #${prNumber}
- Title: ${pr.title}
- Author: ${pr.user?.login}
- Base branch: ${pr.base.ref}
- Head branch: ${pr.head.ref}
- URL: ${pr.html_url}
- Description:
${pr.body || "No PR description provided."}

Current PR existing human comments:
${currentComments || "No comments yet."}

Recent merged PR memory and previous review/comment style:
${recentMemory || "No recent PR memory found."}

Changed files summary:
${trimLargeInput(fileSummary, 30000)}

Unified PR diff:
${trimLargeInput(diff, 50000)}

Important instructions:
- Review only what is visible in this PR diff and supplied context.
- Do not invent line numbers if exact lines are not visible.
- Use file paths and changed sections wherever possible.
- If the diff is truncated, mention that the review may be incomplete.
- Do not approve if there are meaningful correctness, security, or maintainability risks.
- Be strict, but helpful and constructive.
- Do not repeat the full diff.
`;

const review = await callOpenAI(prompt);

const finalBody = `<!-- ai-pr-reviewer -->

# AI PR Review

${review}

---

_Review generated automatically by CampusFlow AI PR Reviewer._

Workflow run: ${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}
`;

await upsertPrComment(finalBody);

console.log("AI PR review comment posted successfully.");