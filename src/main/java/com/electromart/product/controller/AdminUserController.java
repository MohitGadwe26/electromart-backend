// package com.electromart.controller;

// import java.util.List;

// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.electromart.common.entity.User;
// import com.electromart.service.AdminUserService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/admin/users")
// @RequiredArgsConstructor
// @CrossOrigin
// public class AdminUserController {

//     private final AdminUserService adminUserService;

//     // ✔ View all users
//     @GetMapping
//     public List<User> getAllUsers() {
//         return adminUserService.getAllUsers();
//     }

//     // ✔ Block / Unblock
//     @PatchMapping("/{id}/status")
//     public void updateStatus(
//             @PathVariable Long id,
//             @RequestParam boolean enabled) {
//         adminUserService.updateUserStatus(id, enabled);
//     }

//     // ✔ Assign role
//     @PatchMapping("/{id}/role")
//     public void updateRole(
//             @PathVariable Long id,
//             @RequestParam String role) {
//         adminUserService.updateUserRole(id, role);
//     }

//     // ✔ Soft delete
//     @DeleteMapping("/{id}")
//     public void deleteUser(@PathVariable Long id) {
//         adminUserService.deleteUser(id);
//     }
// }
