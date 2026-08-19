package com.vmi.policyapi.common.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
class CacheConfig {

	// default: cache 10 นาที + serialize เป็น JSON (อ่าน debug ใน redis-cli ได้ ไม่ใช่ JDK
	// binary serialization ที่ผูกกับ classpath เวอร์ชันเป๊ะๆ) ทุก @Cacheable ใช้ config นี้
	// ร่วมกันถ้าไม่ระบุ cache name เฉพาะเจาะจงเพิ่มเติม
	@Bean
	RedisCacheConfiguration cacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(10))
			.disableCachingNullValues()
			.serializeValuesWith(RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	}

}
