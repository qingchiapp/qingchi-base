package com.qingchi.base.repository.talk;

import com.qingchi.base.constant.CommonStatus;
import com.qingchi.base.constant.status.ContentStatus;
import com.qingchi.base.model.BaseModelDO;
import com.qingchi.base.model.talk.CommentDO;
import com.qingchi.base.model.talk.TalkDO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TODO〈一句话功能简述〉
 * TODO〈功能详细描述〉
 *
 * @author qinkaiyuan
 * @since TODO[起始版本号]
 */
public interface CommentRepository extends JpaRepository<CommentDO, Integer> {
    Optional<BaseModelDO> findOneByIdAndStatus(Integer id, String status);

    @Caching(evict = {
            //用户的talks肯定变化了
            //新增一条数据肯定所有数据清空，数据的显示数据变了
            @CacheEvict(cacheNames = "talkById", key = "#comment.talkId"),
            //新增一条数据肯定所有数据清空，数据的显示数据变了
            @CacheEvict(cacheNames = "talkComments5", key = "#comment.talkId"),
            @CacheEvict(cacheNames = "talkComments50", key = "#comment.talkId"),
            @CacheEvict(cacheNames = "commentComments3", key = "#comment.parentCommentId", condition="#comment.parentCommentId!=null"),
            @CacheEvict(cacheNames = "commentComments50", key = "#comment.parentCommentId", condition="#comment.parentCommentId!=null")
    })
    CommentDO save(CommentDO comment);

    /**
     * 展示评论
     *
     * @param talkId
     * @return
     */
    @Cacheable(cacheNames = "talkComments5", key = "{#talkId}")
    List<CommentDO> findTop5ByTalkIdAndStatusInAndParentCommentIdIsNullOrderByUpdateTimeDesc(Integer talkId, List<String> status);

    /**
     * talk详情页展示评论
     *
     * @param talkId
     * @return
     */
    @Cacheable(cacheNames = "talkComments50", key = "{#talkId}")
    List<CommentDO> findTop50ByTalkIdAndStatusInAndParentCommentIdIsNullOrderByUpdateTimeDesc(Integer talkId, List<String> status);

    @Cacheable(cacheNames = "commentComments3", key = "{#commentId}")
    List<CommentDO> findTop3ByParentCommentIdAndStatusInOrderByUpdateTimeDesc(Integer commentId, List<String> status);

    /**
     * 展示子回复,避免性能问题，限制最多50条，再多不如私聊了
     *
     * @param commentId
     * @return
     */
    @Cacheable(cacheNames = "commentComments50", key = "{#commentId}")
    List<CommentDO> findTop50ByParentCommentIdAndStatusInOrderByUpdateTimeDesc(Integer commentId, List<String> status);

    @Transactional
    @Modifying
    @Query("update CommentDO t set t.status = '" + ContentStatus.delete + "' where t.userId=:userId and t.status in (:status)")
    Integer updateUserCommentStatusIn(@Param("userId") Integer userId, @Param("status") List<String> status);

    Optional<CommentDO> findOneByIdAndStatusIn(Integer id, List<String> status);

    //获取最新的评论
    CommentDO findFirstByTalkIdOrderByIdDesc(Integer talkId);


    List<CommentDO> findByParentCommentId(Integer commentId);


    //查询关键词触发次数时使用
    Page<CommentDO> findByStatusNotInOrderByIdDesc(Pageable pageable, List<String> status);
}