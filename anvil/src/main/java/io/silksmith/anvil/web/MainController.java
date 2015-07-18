package io.silksmith.anvil.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.silmith.tooling.model.SilksmithModel;

@RestController
public class MainController {

	@Autowired
	SilksmithModel model;
	
	
	@RequestMapping("/")
	public String index() {
		
		return "hellO";
	}
}
