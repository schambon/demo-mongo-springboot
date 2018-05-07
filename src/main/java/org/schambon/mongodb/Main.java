package org.schambon.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication
public class Main {

    @Value("${mongodb.uri}") String mongodbUri;

    @Bean
    public MongoClient mongoClient() {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        return new MongoClient(new MongoClientURI(mongodbUri, MongoClientOptions.builder().codecRegistry(pojoCodecRegistry)));
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
