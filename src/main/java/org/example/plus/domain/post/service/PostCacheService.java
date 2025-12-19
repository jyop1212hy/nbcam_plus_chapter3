package org.example.plus.domain.post.service;

import lombok.RequiredArgsConstructor;
import org.example.plus.domain.post.model.dto.PostDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_POST_PREFIX = "post";
    private static final String RANKING_POST_KEY = "ranking:posts";

    // 캐시를 조회 하는것
    public PostDto getPostCache(Long postId) {
        String key = CACHE_POST_PREFIX + postId;

        return (PostDto) redisTemplate.opsForValue().get(key);
    }

    // 캐시를 저장 하는것
    public void savePostCache(Long postId, PostDto postDto) {
        String key = CACHE_POST_PREFIX + postId;
        redisTemplate.opsForValue().set(key, postDto,10, TimeUnit.MINUTES);
    }

    //개시 삭제하는 곳(무효화)
    public void deletePostCache(Long postId) {
        String key = CACHE_POST_PREFIX + postId;
        redisTemplate.delete(key);
    }

    //조회된 게시글의 조회수 증가
    public void increaseViewCount(Long postId) {
        redisTemplate.opsForZSet().incrementScore(RANKING_POST_KEY, postId.toString(),1);
    }

    //인기 게시글 조회
    public List<Long> getTopPostList(Integer limit) {
        Set<Object> postIdList = redisTemplate.opsForZSet().reverseRange(RANKING_POST_KEY, 0,limit -1);

        //NPE 방어 코드
        if(postIdList == null || postIdList.isEmpty()) {
            return Collections.emptyList();
        }

        return postIdList.stream()
                .map(id -> Long.parseLong(id.toString()))
                .toList();
    }


}
