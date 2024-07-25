package com.mytech.restaurantportal.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.DocumentException;
import com.mytech.restaurantportal.exporters.OrderPdfExporter;
import com.mytech.restaurantportal.helpers.AppConstant;
import com.restaurant.service.entities.Order;
import com.restaurant.service.entities.OrderItem;
import com.restaurant.service.paging.PagingAndSortingHelper;
import com.restaurant.service.paging.PagingAndSortingParam;
import com.restaurant.service.services.OrderItemService;
import com.restaurant.service.services.OrderService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/order")
public class OrderController {

	private String defaultRedirectURL = "redirect:/order/page/1?sortField=ordertime&sortDir=asc";
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;

	@GetMapping("")
	public String getOrderList(@RequestParam(name = "date", required = false) String date, Model model) {
	    if (date == null || date.isEmpty()) {
	        date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    }
	    
	    List<Order> orders = orderService.getAllOrder();
	    model.addAttribute("orders", orders);
	    model.addAttribute("currentPage", "1");
	    model.addAttribute("sortField", "");
	    model.addAttribute("sortDir", "asc");
	    model.addAttribute("reverseSortDir", "desc");
	    model.addAttribute("searchText", "");
	    model.addAttribute("moduleURL", "/order");
	    model.addAttribute("date", date);
	    
	    return "/apps/order/list"; 
	}

	@GetMapping("/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "orders", moduleURL = "/order", defaultSortField = "ordertime") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			@RequestParam(name = "sortField", defaultValue = "ordertime", required = false) String sortField,
			@RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {

		System.out.println(
				"OrderController listByPage::" + pageNum + " sortField: " + sortField + " sortDir: " + sortDir);
		orderService.listOrderByPage(pageNum, AppConstant.pageCount, helper);

		return "/apps/order/list";
	}

	@GetMapping("/page")
	public String listByPage() {
		return defaultRedirectURL;
	}


	@GetMapping("/{orderId}/items")
	public String getOrderItemsByOrderId(@PathVariable("orderId") Long orderId, Model model) {
	    List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId)
	                                                 .stream()
	                                                 .filter(orderItem -> orderItem.getIngredient() != null)
	                                                 .collect(Collectors.toList());
	    model.addAttribute("orderItems", orderItems);
	    return "/apps/order/order-items";
	}


	@GetMapping("/export/pdf/{orderId}")
	public void exportOrderItemsToPDF(@PathVariable("orderId") Long orderId, HttpServletResponse response)
			throws IOException, DocumentException {
		List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
		OrderPdfExporter exporter = new OrderPdfExporter();
		exporter.export(orderItems, response);
	}

	@PostMapping("/export")
	public void exportOrderItems(HttpServletResponse response, @RequestParam(name = "format") String format,
			@RequestParam(name = "orderId") Long orderId) throws IOException, DocumentException {
		System.out.println("Order export: " + format + " for order ID: " + orderId);

		List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);

		OrderPdfExporter exporter = new OrderPdfExporter();
		exporter.export(orderItems, response);
	}
}
