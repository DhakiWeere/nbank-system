package interceptor;

import annotation.ServiceCallLog;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@ServiceCallLog
@Interceptor
public class ServiceCallLogInterceptor {

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String className = context.getTarget().getClass().getSimpleName();
        String methodName = context.getMethod().getName();
        Object[] params = context.getParameters();
        String timestamp = LocalDateTime.now().toString();

        System.out.printf(">>> [%s] Called %s.%s(%s)%n", timestamp, className, methodName, Arrays.toString(params));

        Object result = context.proceed();

        System.out.printf("<<< [%s] Finished %s.%s%n", LocalDateTime.now(), className, methodName);

        return result;
    }
}
