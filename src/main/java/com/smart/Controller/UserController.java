package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public  void addCommmonData(Model m,Principal principal)
	{
		String userName=principal.getName();
	
		User u=userRepository.getUserByName(userName);
	
		m.addAttribute("user",u);
	}
	@RequestMapping("/index")
	public String dashboard()
	{
		
		return "Normal/user_dashboard";
	}
	@RequestMapping("/add-contact")
	public String openConatctForm(Model m)
	{
		
	    m.addAttribute("contact",new Contact());
		return "Normal/add_contact";
	}
	@PostMapping("/process-contact")
	public String processForm(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file ,Principal principal,HttpSession session)
	{
		try
		{
			
			
			
			if(file.isEmpty())
			{
				contact.setImage("contact.png");
				
			    User user=this.userRepository.getUserByName(principal.getName());
				user.getContacts().add(contact);
				contact.setUser(user);
				this.userRepository.save(user);
			
				session.setAttribute("message", new Message("Contact successfully added","success"));
				
			}
			else
			{
				contact.setImage(file.getOriginalFilename());
			
			File file1=new ClassPathResource("/static/img").getFile();
			
		     Path path=Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		    User user=this.userRepository.getUserByName(principal.getName());
			user.getContacts().add(contact);
			contact.setUser(user);
			this.userRepository.save(user);
		
			session.setAttribute("message", new Message("Contact successfully added","success"));
			}
		     
				
			
			
			
		} 
		
		catch (Exception e) {
			System.out.println("error"+e.getMessage());
			session.setAttribute("message", new Message("Something is wrong","danger"));
			e.printStackTrace();
		}
		
	
		return "Normal/add_contact";
	}
	
	
	@GetMapping("/show-contacts/{page}")
	public String show(@PathVariable("page") Integer page,Principal p,Model m)
	{
		String user=p.getName();
		User u=userRepository.getUserByName(user);
	
	  Pageable pageable=PageRequest.of(page, 5);
		Page<Contact> contacts=this.contactRepository.findContactByUser(u.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "Normal/show-contacts";
	}
	@GetMapping("/{cId}/contact/")
	public String showContact(@PathVariable("cId") int cId,Model m,Principal principal)
	{
		Contact contact=this.contactRepository.getById(cId);
		
		String userName=principal.getName();
		User u=this.userRepository.getUserByName(userName);
		if(u.getId()==contact.getUser().getId())
		{
			m.addAttribute("contact",contact);
			
		}
		
		
		return "Normal/contactDetail.html";
	}
@GetMapping("/delete/{cId}")
public String deleteContact(@PathVariable("cId") int cId,Model m,HttpSession session,Principal principal)
{
	Optional<Contact> opt=this.contactRepository.findById(cId);
	Contact contact=opt.get();
	User user=userRepository.getUserByName(principal.getName());
	user.getContacts().remove(contact);
this.userRepository.save(user);
	session.setAttribute("message", new Message("Deleted Succesfully","alert-danger"));
	return "redirect:/user/show-contacts/0";
	
}
@PostMapping("/update-contact/{cId}")
public String updateForm(@PathVariable("cId") int cId, Model m,HttpSession session)
{
	Optional<Contact> opt=this.contactRepository.findById(cId);
	Contact contact=opt.get();
	m.addAttribute("c",contact);
	return "Normal/updateForm";
	
}
@PostMapping("/process-update")
public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file ,Model m,HttpSession session,Principal principal)
{

	 User user=this.userRepository.getUserByName(principal.getName());
	 System.out.println(contact);
		
		contact.setUser(user);
	   if(file.isEmpty())
	   {
		   Contact contact1=this.contactRepository.getById(contact.getcId());
		   contact.setImage(contact1.getImage());
	   }
	   else
	   {
		   try
		   {
		   contact.setImage(file.getOriginalFilename());
			
			File file1=new ClassPathResource("/static/img").getFile();
			
		     Path path=Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		   }
		   catch (Exception e) {
			// TODO: handle exception
		}
	   }
		contactRepository.save(contact);
	
		m.addAttribute("c",new Contact());
		
	session.setAttribute("message", new Message("Updated successfully","alert-danger"));
	return "Normal/updateForm";
	
}
@RequestMapping("/profile")
public String updateForm(Principal principal,Model m)
{
	 User user=this.userRepository.getUserByName(principal.getName());
	 m.addAttribute("contact",user);
	 System.out.println("fuckkkkkkk");
	 System.out.println(principal.getName());
	 
	 
	return "Normal/profile";
	
}

}
