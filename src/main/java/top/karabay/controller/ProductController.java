package top.karabay.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import top.karabay.model.Product;
import top.karabay.service.PayService;
import top.karabay.service.ProductService;

@Controller
public class ProductController {

	@Resource
	private ProductService productService;

	@Resource
	private PayService payService;

	@RequestMapping("search")
	public String search(HttpServletRequest request, Model model) {
		String productDesc = request.getParameter("desc");

		if (productDesc != null) {
			List<Product> productList = productService.getProductListByDesc(productDesc);
			model.addAttribute("productList", productList);
			return "product_search_ok";
		}

		model.addAttribute("error", "desc == null");
		return "failed";
	}

	@RequestMapping("prepareForPay")
	public String prepareForPay(HttpServletRequest request, Model model) {
		try {
			int productId = Integer.valueOf(request.getParameter("id"));
			if (productId != 0) {
				boolean available = productService.isAvailable(productId);
				if (available) {
					// orderService.order(userId, productId);
					Product product = productService.getProductById(productId);
					if (product != null) {
						model.addAttribute("product", product);
						return "prepareForPay_ok";
					}
					model.addAttribute("error", "product == null");
					return "failed";
				}
				model.addAttribute("error", "商品已售罄");
				return "failed";
			}
			model.addAttribute("error", "productId == 0");
			return "failed";
		} catch (Exception e) {
			model.addAttribute("error", e.toString());
			return "failed";
		}
	}

	@RequestMapping("pay")
	public String pay(HttpServletRequest request, Model model) {
		try {
			int productId = Integer.valueOf(request.getParameter("productId"));
			int payMount = Integer.valueOf(request.getParameter("payMount"));
			if (productId != 0 && payMount != 0) {
				String payAccount = request.getParameter("payAccount");
				String payPassword = request.getParameter("payPassword");
				String payResult = payService.pay(payAccount, payPassword, payMount, productId);
				if (payResult != null) {
					if (payResult.equals("ok")) {
						model.addAttribute("message", "pay ok");
						return "pay_ok";
					}
					model.addAttribute("error", payResult);
					return "failed";
				}
				model.addAttribute("error", "payResult == null");
				return "failed";
			}
			model.addAttribute("error", "productId == 0");
			return "failed";
		} catch (Exception e) {
			model.addAttribute("error", e.toString());
			return "failed";
		}
	}
}
