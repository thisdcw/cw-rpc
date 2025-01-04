import cn.hutool.core.util.IdUtil;
import com.cw.core.constant.RpcConstant;
import com.cw.core.fault.retry.FixedIntervalRetryStrategy;
import com.cw.core.fault.retry.NoRetryStrategy;
import com.cw.core.fault.retry.RetryStrategy;
import com.cw.core.loadbalancer.ConsistenHashLoadBalancer;
import com.cw.core.loadbalancer.LoadBalancer;
import com.cw.core.loadbalancer.RandomLoadBalancer;
import com.cw.core.loadbalancer.RoundRobinLoadBalancer;
import com.cw.core.model.RpcRequest;
import com.cw.core.model.RpcResponse;
import com.cw.core.model.ServiceMetaInfo;
import com.cw.core.protocol.*;
import io.vertx.core.buffer.Buffer;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thisdcw-com
 * @date 2024/10/18 16:21
 */
public class LoadTest {
    final static LoadBalancer loadBalancer = new ConsistenHashLoadBalancer();

    public static void main(String[] args) throws ClassNotFoundException, IOException {


        RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

        try {
            RpcResponse rpcResponse = retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }


    }
}
