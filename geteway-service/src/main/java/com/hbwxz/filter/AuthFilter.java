package com.hbwxz.filter;


import com.hbwxz.feignclient.Oauth2ServiceClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * filter一定需要添加@Component注解，不然不会生效
 * @author Night
 * @date 2023/7/14 8:12
 */
@Component
public class AuthFilter implements GlobalFilter,Ordered {

    /**
     * 问题 2.3
     * 如果“在gateway中”，通过Autowired我们引入 feignclient，会发生死锁。springcloud gateway人家是
     * 基于netty的，人加是webFlux的， 人家是响应式的编程。你这么唐突的引入feignclient，在代码设计层面Loaded RoutePredicateFactory
     * 会造成死锁，导致无法启动
     * 可是gateway这东西，很有可能会需要进行rest形式的一些调用啊，比如现在的我们通过filter去checktoken，这是
     * 很常见的一个需要，很合理，你不让我用吗？
     * 这个问题解决，只能错峰，
     * 加@Lazy注解，延迟加载，这样就不会造成死锁了
     */
    @Autowired
    @Lazy
    private Oauth2ServiceClient oauth2ServiceClient;

    /**
     *
     * @param exchange ServerWebExchange exchange = authorizationContext.getExchange();
     * @param chain 处理链条
     * @return
     */
    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        // 建议用以下方式判断，也可以用正则
        if(path.contains("/oauth")
                || path.contains("/user/register")) {
            return chain.filter(exchange);
        }
        // 从请求头中取出token
        String token = request.getHeaders().getFirst("Authorization");
        // 问题 2.4 block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-http-nio-3 reactor
        // 响应式编程就是基于 reactor的.openfeign的rest形式进行checktoken的调用，就是命令式编程，你这不跟响应式编程对这干呢么。
        // Map<String, Object> result = oauth2ServiceClient.checkToken(token);

        // 异步调用
        CompletableFuture<Map> future = CompletableFuture.supplyAsync(() -> oauth2ServiceClient.checkToken(token));
        Map<String, Object> result = future.get();
        boolean active = (boolean) result.get("active");
        if (!active){
            // 将token放入请求头中
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 比如说我们可以给微服务转发请求的时候带上一些header信息
        ServerHttpRequest httpRequest = request.mutate().headers(httpHeaders -> {
            httpHeaders.set("personId", request.getHeaders().getFirst("personId"));
            // httpHeaders.set("tracingId", "");
        }).build();
        exchange.mutate().request(httpRequest);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
