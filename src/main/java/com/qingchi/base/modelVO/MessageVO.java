package com.qingchi.base.modelVO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qingchi.base.constant.CommonStatus;
import com.qingchi.base.model.chat.MessageDO;
import com.qingchi.base.model.chat.MessageReceiveDO;
import com.qingchi.base.repository.chat.MessageRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author qinkaiyuan
 * @date 2019-08-14 17:24
 */
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Component
public class MessageVO {
    private static MessageRepository messageRepository;

    @Resource
    public void setMessageRepository(MessageRepository messageRepository) {
        MessageVO.messageRepository = messageRepository;
    }

    // 前端遍历使用
    private Long id;
    private String content;
    private MessageUserVO user;
    // 前端对比是否为当前用户使用，聊天页面
    //用来帮助前端判断是否为自己发送的
    private Boolean isMine;
    private String readStatus;
    private Boolean isRead;
    private Integer readNum;
    private Date createTime;
    private Date updateTIme;
    private String type;

    public MessageVO() {
    }

    public MessageVO(MessageDO messageDO) {
        this.id = messageDO.getId();
        this.content = messageDO.getContent();
        this.createTime = messageDO.getCreateTime();


        //消息的用户
        this.user = new MessageUserVO(messageDO.getUserId());
        this.isMine = false;
        this.readStatus = CommonStatus.sended;
        this.isRead = true;
        this.readNum = messageDO.getReadNum();
        this.type = messageDO.getType();
    }

    //websocket推新消息时设置为未读
    public MessageVO(MessageDO messageDO, boolean readFlag) {
        this(messageDO);
        this.isRead = false;
    }

    public MessageVO(MessageDO messageDO, String mineFlag) {
        this(messageDO);
        this.isMine = true;
    }

    public MessageVO(MessageDO messageDO, Integer userId) {
        this(messageDO);
        this.isMine = this.user.getId().equals(userId);
    }

    public MessageVO(MessageReceiveDO messageReceive, MessageDO messageDO) {
        this(messageDO);
        //涉及到举报，不知道是msgid还是msguserid，所以暂时取消，统一使用msgid，删除和举报
//        this.id = messageReceive.getId();
        this.isMine = messageReceive.getIsMine();
        if (this.isMine) {
            //自己发的消息就去msg上的发送状态
            this.readStatus = messageDO.getReadStatus();
        } else {
            //否则就取自己的阅读状态
            this.isRead = messageReceive.getIsRead();
        }
        //待定不知道这个是干嘛的
//            this.updateTIme = messageDO.getUpdateTime();
    }

    public static List<MessageVO> messageReceiveDOToVOS(List<MessageReceiveDO> messageDOS) {
        //翻转数组,因为查出来的是倒序的
        List<MessageVO> messageVOS = messageDOS.stream().map(messageReceiveDO -> {
            Optional<MessageDO> optionalMessageDO = messageRepository.findById(messageReceiveDO.getMessageId());
            return new MessageVO(messageReceiveDO, optionalMessageDO.get());
        }).collect(Collectors.toList());
        Collections.reverse(messageVOS);
        return messageVOS;
    }

    //未登录的消息
    public static List<MessageVO> messageDOToVOS(List<MessageDO> messageDOS) {
        //翻转数组,因为查出来的是倒序的
        List<MessageVO> messageVOS = messageDOS.stream().map(MessageVO::new).collect(Collectors.toList());
        Collections.reverse(messageVOS);
        return messageVOS;
    }

    public static List<MessageVO> messageDOToVOS(List<MessageDO> messageDOS, Integer userId) {
        //翻转数组,因为查出来的是倒序的 (ChatUserDO chatUserDO) -> new ChatVO(chatUserDO, true)
        List<MessageVO> messageVOS = messageDOS.stream().map((MessageDO msg) -> new MessageVO(msg, userId)).collect(Collectors.toList());
        Collections.reverse(messageVOS);
        return messageVOS;
    }
}