import { Approval, CampusRequest } from '../../../core/models/request.model';

export interface RequestDetailViewModel {
  request: CampusRequest;
  approvals: Approval[];
}
