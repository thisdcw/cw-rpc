import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author thisdcw-com
 * @date 2024/10/18 16:21
 */
public class LoadTest {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.apache.curator.retry.ExponentialBackoffRetry", true, Thread.currentThread().getContextClassLoader());
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    }
}
