package acauhi.mvc.spring.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (status != null) {
      int statusCode = Integer.parseInt(status.toString());

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        return "pages/not-found";
      } else if (statusCode == HttpStatus.FORBIDDEN.value() || statusCode == HttpStatus.UNAUTHORIZED.value()) {
        return "pages/access-denied";
      }
    }

    return "pages/error";
  }
}