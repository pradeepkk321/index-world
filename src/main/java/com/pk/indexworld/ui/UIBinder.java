package com.pk.indexworld.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pk.indexworld.indexer.IndexerNew;

@Controller
public class UIBinder {

	@GetMapping("/index")
	public String showUserList(Model model) {
		
		model.addAttribute("collections", IndexerNew.indexPage());
		model.addAttribute("view", "home"); 
		
		return "default";
	}

	@GetMapping("/document")
	public String displayDocument(String name, String id, Model model) {
		
		model.addAttribute("collections", IndexerNew.indexPage());
		model.addAttribute("doc1", IndexerNew.document(name, id));
		model.addAttribute("document", IndexerNew.document1(name, id));
		
		model.addAttribute("view", "document"); 
		
		return "default";
	}
	
	@GetMapping("/schema")
	public String displaySchema(String name, Model model) {
		
		model.addAttribute("collections", IndexerNew.indexPage());
		model.addAttribute("document", IndexerNew.schema(name));
		
		model.addAttribute("view", "document"); 
		
		return "default";
	}
}
