package sms.webservice;

import com.vonage.client.VonageClient;
import com.vonage.client.VonageClientException;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

public class SendSMSServlet extends HttpServlet {
    private String VONAGE_BRAND_NAME;
    private VonageClient client;

    public void init(ServletConfig config) {
        // Load configuration from the servlet container:
        VONAGE_BRAND_NAME = config.getInitParameter("from_number");
        String api_key = config.getInitParameter("api_key");
        String api_secret = config.getInitParameter("api_secret");

        client = VonageClient.builder().apiKey(api_key).apiSecret(api_secret).build();
    }

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException,
            java.io.IOException {
        try {
            // Extract form parameters from the request:
            String to_number = req.getParameter("to");
            String message = req.getParameter("message");

            TextMessage sms = new TextMessage(VONAGE_BRAND_NAME,
                    to_number,
                    message
            );

            SmsSubmissionResponse response = client.getSmsClient().submitMessage(sms);

            if (response.getMessages().get(0).getStatus() == MessageStatus.OK) {
                System.out.println("Message sent successfully.");

                resp.getWriter().println(response.getMessages().get(0));
            } else {
                System.out.println("Message failed with error: " + response.getMessages().get(0).getErrorText());
            }

        } catch (VonageClientException nce) {
            throw new ServletException(nce);
        }
    }
}