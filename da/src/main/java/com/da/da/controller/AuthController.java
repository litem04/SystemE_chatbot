//package com.da.da.controller;
//
//
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import com.da.da.entity.Customer;
//import com.da.da.entity.Admin;
//import com.da.da.repository.CustomerRepository;
//import com.da.da.repository.AdminRepository;
//import java.util.Date;
//
//@Controller
//public class AuthController {
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    // 1. Hiển thị trang Đăng ký (GET)
//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        model.addAttribute("customer", new Customer()); // Gửi một object rỗng sang form
//        return "client/customer-register"; // Trả về file customer-register.html
//    }
//
//    // 2. Xử lý Đăng ký (POST) - Đây là phần thay thế cho hàm doPost trong Servlet cũ
//    @PostMapping("/register")
//    public String registerCustomer(@ModelAttribute Customer customer, HttpSession session) {
//        try {
//            // Kiểm tra email đã tồn tại chưa
//            Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
//            if (existingCustomer != null) {
//                session.setAttribute("fail-message", "Email này đã được đăng ký!");
//                return "redirect:/register";
//            }
//
//            // Gán ngày tạo (nếu form không gửi lên)
//            customer.setAddedDate(new Date());
//
//            // Lưu vào Database (Thay thế đoạn SQL Insert thủ công)
//            customerRepository.save(customer);
//
//            // Thông báo thành công
//            session.setAttribute("success-message", "Đăng ký thành công! Vui lòng đăng nhập.");
//            return "redirect:/login"; // Chuyển hướng sang trang đăng nhập
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            session.setAttribute("fail-message", "Đăng ký thất bại: " + e.getMessage());
//            return "redirect:/register";
//        }
//    }
//    
//    // 3. Hiển thị trang Đăng nhập (GET) - Để redirect sang sau khi đăng ký xong
////    @GetMapping("/login")
////    public String showLoginForm() {
////        return "client/customer-login";
////    }
//    @Autowired private CustomerRepository customerRepo;
//    @Autowired private AdminRepository adminRepo;
//
//    // --- PHẦN 1: KHÁCH HÀNG (CUSTOMER) ---
//
//    // Hiện form login khách
//    @GetMapping("/login")
//    public String showCustomerLogin() {
//        return "client/customer-login"; // Trả về file html
//    }
//
//    // Xử lý login khách (Thay thế CustomerLogin Servlet)
//    @PostMapping("/login")
//    public String processCustomerLogin(@RequestParam String email, 
//                                       @RequestParam String password, 
//                                       HttpSession session, 
//                                       Model model) {
//        // 1. Tìm trong DB
//        Customer customer = customerRepo.findByEmail(email);
//
//        // 2. Kiểm tra mật khẩu (Lưu ý: Đang so sánh plain text theo DB của bạn)
//        if (customer != null && customer.getPassword().equals(password)) {
//            // Đúng mật khẩu -> Lưu session
//            session.setAttribute("user", customer);
//            session.setAttribute("role", "CUSTOMER");
//            return "redirect:/"; // Về trang chủ
//        } else {
//            // Sai -> Báo lỗi
//            model.addAttribute("error", "Sai email hoặc mật khẩu!");
//            return "client/customer-login";
//        }
//    }
//
//    // --- PHẦN 2: QUẢN TRỊ VIÊN (ADMIN) ---
//
//    // Hiện form login admin
//    @GetMapping("/admin/login")
//    public String showAdminLogin() {
//        return "admin/admin-login"; // Trả về file html
//    }
//
//    // Xử lý login admin (Thay thế AdminLogin Servlet)
//    @PostMapping("/admin/login")
//    public String processAdminLogin(@RequestParam String email, 
//                                    @RequestParam String password, 
//                                    HttpSession session, 
//                                    Model model) {
//        Admin admin = adminRepo.findByEmail(email);
//
//        if (admin != null && admin.getPassword().equals(password)) {
//            session.setAttribute("admin", admin);
//            session.setAttribute("role", "ADMIN");
//            return "redirect:/admin/dashboard"; // Vào trang quản trị
//        } else {
//            model.addAttribute("error", "Sai tài khoản Admin!");
//            return "admin/admin-login";
//        }
//    }
//    

//}

package com.da.da.controller;

import com.da.da.entity.Customer;
import com.da.da.repository.CustomerRepository;
import com.da.da.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Date;

@Controller
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpSession session) {
       
    	if (session.getAttribute("fail-message") != null) {
    	    String error = (String) session.getAttribute("fail-message");
    	    
    	    // SỬA DÒNG NÀY (Bỏ dấu gạch ngang):
    	    model.addAttribute("failMessage", error); 
    	    
    	    session.removeAttribute("fail-message");
    	}

    	// Sửa key: "success-message" -> "successMessage"
    	if (session.getAttribute("success-message") != null) {
    	    String success = (String) session.getAttribute("success-message");
    	    
    	    // SỬA DÒNG NÀY (Bỏ dấu gạch ngang):
    	    model.addAttribute("successMessage", success);
    	    
    	    session.removeAttribute("success-message");
    	}

        model.addAttribute("customer", new Customer()); // (Code cũ của bạn giữ nguyên)
        return "client/customer-register";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute Customer customer, HttpSession session) {
        try {
            Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer != null) {
                session.setAttribute("fail-message", "Email này đã được đăng ký!");
                return "redirect:/register";
            }
            customer.setAddedDate(new Date());
            customerRepository.save(customer);
            session.setAttribute("success-message", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login"; 
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("fail-message", "Lỗi: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // --- 2. ĐĂNG NHẬP (Chỉ giữ lại 1 hàm hiện Form) ---
    // Spring Security sẽ tự xử lý POST tại /do-login, bạn không cần viết Controller cho nó
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "client/customer-login"; // Trỏ về file html đăng nhập chung
    }

	  // --- ĐĂNG XUẤT CHUNG ---
	  @GetMapping("/logout")
	  public String logout(HttpSession session) {
	      session.invalidate(); // Xóa sạch session
	      return "redirect:/";
	  }
	  @Autowired
	    private CustomUserDetailsService customerService;
	 // 1. Hiển thị trang hồ sơ
	    @GetMapping("/profile")
	    public String showProfile(Model model, Principal principal) {
	        if (principal == null) {
	            return "redirect:/login"; // Chưa đăng nhập thì đá về login
	        }
	        
	        String email = principal.getName(); // Lấy email người đang đăng nhập
	        Customer customer = customerService.findByEmail(email);
	        
	        model.addAttribute("customer", customer);
	        model.addAttribute("title", "Hồ sơ cá nhân");
	        return "client/profile"; // Trả về file HTML profile (bạn cần tạo file này)
	    }

	    // 2. Xử lý cập nhật hồ sơ
	    @PostMapping("/update-profile")
	    public String updateProfile(@ModelAttribute("customer") Customer formCustomer, 
	                                Principal principal, 
	                                Model model) {
	        if (principal == null) {
	            return "redirect:/login";
	        }

	        String email = principal.getName();
	        Customer currentCustomer = customerService.findByEmail(email);

	        // Gọi service để update
	        // Lưu ý: customerService cần logic mapping dữ liệu như Bước 1
	        customerService.updateProfile(currentCustomer, formCustomer); 
	        
	        model.addAttribute("message", "Cập nhật thông tin thành công!");
	        model.addAttribute("customer", currentCustomer); // Load lại data mới
	        
	        return "client/profile"; // Ở lại trang và báo thành công
	    }
}