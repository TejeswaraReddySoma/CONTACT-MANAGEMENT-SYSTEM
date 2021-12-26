package com.smart.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

  @GetMapping("/")
  public String home(Model m)
  {
	  m.addAttribute("title","teja");
	  return "home";
  }
  @GetMapping("/about")
  public String about(Model m)
  {
	
	  return "about";
  }
  @GetMapping("/signup")
  public String signup(Model m)
  {
	 User u=new User();
	  m.addAttribute("user",u);
	
	  return "signup";
  }
  @PostMapping("/do_register")
  public String register(@Valid @ModelAttribute("user") User user,BindingResult res1,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model m,HttpSession session)
{
	  System.out.println("Errors1");  
	
       try {
    	   if(agreement==false)
    	   {
    		 throw new Exception("fuckkkk");
    	   }
    	
    	   if(res1.hasErrors())
    	   {
    		
    		   m.addAttribute("user",user);
    		   return "signup";
    	   }
    	  
    	   user.setEnabled(true);
    	 user.setRole("ROLE_USER");
    	 user.setImageUrl("default.png");
    	 user.setPassword(passwordEncoder.encode(user.getPassword()));
    	   	User resu=this.userRepository.save(user);
    	   	m.addAttribute("user", new User());
    	 	session.setAttribute("message",new Message("Successfully registered","alert-success"));
    	  
    	   	
    	   
	} 
       
      catch (Exception e) 
    {
	e.printStackTrace();
  	m.addAttribute("user",user);
  	session.setAttribute("message",new Message("Something went wrong","alert-wrong"));
  	return "signup";
	}

	  return "signup";
  }
  @GetMapping("/signin")
  public String login(Model m)
  {
	 User u=new User();
	  m.addAttribute("user",u);
	  
	
	  return "login";
  }
  
}
