package com.example.demo.Config;



import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig{

    public static final String QUEUE = "test_queue";
    public static final String QUEUE2 = "test_queue2";
    public static final String QUEUE_GET = "test_queue_get";
    public static final String QUEUE_POST = "test_queue_post";
    public static final String EXCHANGE = "test_exchange";
    public static final String EXCHNAGE2 = "fanout-exchange";
    public static final String EXCHNAGE3 = "topic-exchange";
    public static final String ROUNTING_KEY = "test_routing_key";
    public static final String ROUNTING_KEY2 = "queue.get.*";
    public static final String ROUNTING_KEY3 = "queue.post.*";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    @Bean
	public Queue queue2() {
		return new Queue(QUEUE2);
	}

    @Bean
	public Queue queue_get() {
		return new Queue(QUEUE_GET);
	}

    @Bean
	public Queue queue_post() {
		return new Queue(QUEUE_POST);
	}

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
	public FanoutExchange exchange2() {
		return new FanoutExchange(EXCHNAGE2);
	}

    @Bean
	public TopicExchange exchange3() {
		return new TopicExchange(EXCHNAGE3);
	}

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange){
        return BindingBuilder
        .bind(queue)
        .to(exchange)
        .with(ROUNTING_KEY);
    }

    @Bean
	public Binding fanout_Binding(Queue queue, FanoutExchange exchange) {
		return BindingBuilder
        .bind(queue)
        .to(exchange);
	}

    @Bean
	public Binding fanout_Binding2(Queue queue2, FanoutExchange exchange) {
		return BindingBuilder
        .bind(queue2)
        .to(exchange);
	}

    @Bean
	Binding get_Binding(Queue queue_get, TopicExchange topicExchange) {
		return BindingBuilder
            .bind(queue_get)
            .to(topicExchange)
            .with(ROUNTING_KEY2);
	}
	
	@Bean
	Binding pos_Binding(Queue queue_post, TopicExchange topicExchange) {
		return BindingBuilder
            .bind(queue_post)
            .to(topicExchange)
            .with(ROUNTING_KEY3);
	}

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}