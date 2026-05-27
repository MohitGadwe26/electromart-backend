package com.electromart.admin.service.notification;

import com.electromart.common.entity.Role;
import com.electromart.common.entity.User;

public interface  NotificationService {
  void notifyStatusChange(User user);
  public void notifyRoleChange(User user, Role oldRole);
   public void notifyRestore(User user) ;
    public void notifyDeletion(User user);
//       public void logUserRoleChange(User admin,
//                                   User user,
//                                   Role oldRole,
//                                   Role newRole);
//      public void logUserStatusChange(User admin,
//                                     User user,
//                                     String reason) ;
//  public void logUserRestore(User admin,
//                                User user);

}
