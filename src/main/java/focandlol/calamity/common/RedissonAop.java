package focandlol.calamity.common;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RedissonAop {
  private final RedissonClient redissonClient;

  @Around("@annotation(redissonLock)")
  public Object lock(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
    System.out.println("락 획득");
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();

    // SpEL 평가
    EvaluationContext context = new StandardEvaluationContext();
    String[] paramNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < args.length; i++) {
      context.setVariable(paramNames[i], args[i]);
    }

    ExpressionParser parser = new SpelExpressionParser();
    String keySuffix = parser.parseExpression(redissonLock.key()).getValue(context, String.class);
    String lockKey = redissonLock.prefix() + ":" + keySuffix;

    RLock lock = redissonClient.getLock(lockKey);
    boolean acquired = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.SECONDS);

    if (!acquired) {
      throw new IllegalStateException("락 획득 실패: " + lockKey);
    }

    try {
      return joinPoint.proceed();
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
        System.out.println("락 종료");
      }
    }
  }
}
