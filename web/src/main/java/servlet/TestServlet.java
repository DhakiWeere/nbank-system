package servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerService;

import java.io.IOException;

@WebServlet("/test-servlet")
public class TestServlet extends HttpServlet {
    @EJB
    CustomerService customerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().printf("<h3>%s</h3>", customerService.authCustomerLoginCredentials("   dhanika", "123456<>"));
        resp.getWriter().printf("<h1>%s</h1>", customerService.getCustomerNameByNIC("200212301067"));

    }
}
