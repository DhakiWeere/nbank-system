package interceptor;

import annotation.SanitizeInput;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ext.InterceptorContext;

@Interceptor
@SanitizeInput
public class InputSanitizerInterceptor {

    @AroundInvoke
    public Object sanitizeInputs(InvocationContext ctx) throws Exception {
        System.out.println("sanitizeInputs: " + ctx.getMethod().getName());

        // get parameter arr
        Object[] params = ctx.getParameters();

        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof String) {
                // STRING PARAM SANITIZE

                String tempParam = (String) params[i];
                params[i] = sanitizeString((String) params[i]);
                //log
                System.out.printf("Sanitized Param : [%s] >>> [%s]",  tempParam, params[i].toString());
            }
        }

        ctx.setParameters(params);
        return ctx.proceed();
    }

    private String sanitizeString(String input) {
        if (input == null) return null;
        return input
                .trim()
                .replaceAll("<[^>]*>", "")  // Remove HTML tags
                .replaceAll("[\"']", "");   // Strip quotes
    }
}
