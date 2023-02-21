package com.pk.indexworld.ui.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pk.indexworld.indexer.IndexerNew;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UiController {

	@GetMapping("/schema/list")
	public Set<String> getAllSchema() {
		return IndexerNew.allData().keySet();
	}
}
