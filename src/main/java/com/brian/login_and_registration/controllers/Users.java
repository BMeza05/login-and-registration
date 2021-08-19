package com.brian.login_and_registration.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brian.login_and_registration.models.User;
import com.brian.login_and_registration.services.UserService;
import com.brian.login_and_registration.validators.UserValidator;

@Controller
public class Users {
    private final UserService userService;
    
    private final UserValidator userValidator;
    
    public Users(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
    
    //Methods that return the forms for the users
    @RequestMapping("/register")
    public String registerForm(@ModelAttribute("user") User user) {
        return "register.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "login.jsp";
     
    }
    //Methods handling the Post requests and saving the data into the sql db
    
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
        // if result has errors, return the registration page (don't worry about validations just now)
        // else, save the user in the database, save the user id in session, and redirect them to the /home route
    	 userValidator.validate(user, result);
         if(result.hasErrors()) {
             return "register.jsp";
         }
         User u = userService.registerUser(user);
         session.setAttribute("user_id", u.getId());
         return "redirect:/home";
     }
    
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") 
    						String email, @RequestParam("password") 
    						String password, Model model, HttpSession session,
    						RedirectAttributes flash) {
        // if the user is authenticated, save their user id in session
    	if(userService.authenticateUser(email, password)) {
    		User thisUser = userService.findByEmail(email);
    		//if there is a user, store in session
    		session.setAttribute("user_id", thisUser.getId());
    		//after the user is stored in session, send them back to the home page logged in
    		return "redirect:/home";
    	}
    	
        // else, add error messages and return the login page
    	else {
    		flash.addFlashAttribute("error", "bruh yo credentials aint valid");
    	}
    	return "redirect:/login";
    }
    
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model ) {
        // get user from session, save them in the model and return the home page
    	Long id=(Long) session.getAttribute("user_id");
    	if(id != null) {
    		User thisUser = userService.findUserById(id);
    		model.addAttribute("user", thisUser);
    		return "home.jsp";
    	}
    	return "redirect:/register";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
        // redirect to login page
    	return "redirect:/login";
    }
}