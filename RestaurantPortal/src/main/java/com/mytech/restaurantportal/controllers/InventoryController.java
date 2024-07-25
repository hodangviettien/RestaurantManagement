
package com.mytech.restaurantportal.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mytech.restaurantportal.helpers.AppConstant;
import com.restaurant.service.entities.FCategory;
import com.restaurant.service.entities.Ingredient;
import com.restaurant.service.paging.PagingAndSortingHelper;
import com.restaurant.service.paging.PagingAndSortingParam;
import com.restaurant.service.services.FCategoryService;
import com.restaurant.service.services.IngredientService;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

	private String defaultRedirectURL = "redirect:/ingredient/page/1?sortField=ingredientCode&sortDir=asc";

	@Autowired
	private IngredientService ingredientService;

	@Autowired
	public FCategoryService fCategoryService;

	@GetMapping("")
	public String getIngredientList(Model model) {
		List<Ingredient> listIng = ingredientService.getAllIngredient();

		List<FCategory> listCate = fCategoryService.getAllCategory();

		model.addAttribute("listIng", listIng);

		model.addAttribute("listCate", listCate);
		model.addAttribute("currentPage", "1");
		model.addAttribute("sortField", "ingredientCode");
		model.addAttribute("sortDir", "asc");
		model.addAttribute("reverseSortDir", "desc");
		model.addAttribute("searchText", "");
		model.addAttribute("moduleURL", "/ingredient");

		return "/apps/inventory/list";
	}

	@GetMapping("/page")
	public String listByPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listIng", moduleURL = "/ingredient", defaultSortField = "ingredientCode") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(name = "sortField", defaultValue = "ingredientCode", required = false) String sortField,
			@RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {

		System.out
				.println("UserControllerlistByPage::" + pageNum + " sortField: " + sortField + " sortDir: " + sortDir);
		ingredientService.listIngByPage(pageNum,  AppConstant.pageCount,helper);

		return "/apps/ingredient/list";
	}

	

	@PostMapping("/updateQuantity")
	public String updateQuantity(@RequestParam("ingredientId") Long ingredientId, @RequestParam("quantity") BigDecimal quantity) {

		System.out.println("Ingredient ID: " + ingredientId);
		ingredientService.updateQuantity(ingredientId, quantity);
		return "redirect:/inventory";
	}

}
