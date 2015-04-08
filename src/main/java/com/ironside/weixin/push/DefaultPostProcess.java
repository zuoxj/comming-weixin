package com.ironside.weixin.push;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.ironside.weixin.push.entity.AbstractBaseEntity;
import com.ironside.weixin.push.entity.EntityEnum;
import com.ironside.weixin.push.entity.EventSubscribeEntity;
import com.ironside.weixin.push.entity.EventUnSubscribeEntity;
import com.ironside.weixin.push.entity.ImageEntity;
import com.ironside.weixin.push.entity.LinkEntity;
import com.ironside.weixin.push.entity.LocationEntity;
import com.ironside.weixin.push.entity.ShortVideoEntity;
import com.ironside.weixin.push.entity.TextEntity;
import com.ironside.weixin.push.entity.VideoEntity;
import com.ironside.weixin.push.entity.VoiceEntity;

/**
 * POST方式推送给微信公众账号的消息处理，具体实现消息解析、处理实体。
 * @author 雪庭
 * @since 1.0 at 2015年4月3日
 */
public class DefaultPostProcess extends AbstractPostProcess {
	
	/** 消息处理器 */
	private IPostProcessor processor;
	

	@Override
	AbstractBaseEntity analyze(String postData) {
		Assert.hasText(postData, "postData 参数不能为空");
		// 将postData解析成properties对象
		Properties properties = doAnalyze(postData);
		// 解析properties对象，建立entity对象
		AbstractBaseEntity entry = doAnalyze(properties);
		return entry; 
	}

	/**
	 * 将postData解析成properties对象
	 * @param postData POST方式推送的数据
	 * @return 解析后的properties对象
	 */
	Properties doAnalyze(String postData) {
		Properties properties = new Properties();
		Document document = null;
		try {
			document = DocumentHelper.parseText(postData);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Assert.notNull(document);
		Element root = document.getRootElement();
		List elements = root.elements();
		Element element;
		for (int i = 0; i < elements.size(); i++) {
			element = (Element)elements.get(i);
			properties.put(element.getName(), element.getText());
		}
		return properties;
	}
	
	/**
	 * 解析properties对象，建立entity对象
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体对象
	 */
	AbstractBaseEntity doAnalyze(Properties properties) {
		Assert.notNull(properties);
		String msgType = properties.getProperty(AbstractBaseEntity.MSG_TYPE);
		
		// 解析事件消息
		if (msgType.equals(EntityEnum.EVENT_CLICK.getMsgType())) {
			return doEventAnalyze(properties); 
		}
		// 解析普通消息
		return doMessageAnalyze(properties);
	}
	
	/**
	 * 解析事件消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体对象
	 */
	private AbstractBaseEntity doEventAnalyze(Properties properties) {
		String event = properties.getProperty(AbstractBaseEntity.EVENT);
		String eventKey = properties.getProperty(AbstractBaseEntity.EVENT_KEY);
		if (event.equals(EntityEnum.EVENT_SUBSCRIBE.getEvent()) && StringUtils.isEmpty(eventKey)) {
			return doEventSubscribeAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_UNSUBSCRIBE.getEvent()) && StringUtils.isEmpty(eventKey)) {
			return doEventUnSubscribeAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_SCAN_SUBSCRIBE.getEvent()) && StringUtils.isEmpty(eventKey)==false) {
			return doEventScanSubscribeAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_SCAN.getEvent())) {
			return doEventScanAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_LOCATION.getEvent())) {
			return doEventLocationAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_CLICK.getEvent())) {
			return doEventClickAnalyze(properties);
		}
		if (event.equals(EntityEnum.EVENT_VIEW.getEvent())) {
			return doEventViewAnalyze(properties);
		}
		throw new IllegalStateException(String.format("解析事件消息出错:(%s)事件类型未知", event));
	}

	/**
	 * 解析关注/取消关注-订阅事件文本消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doEventSubscribeAnalyze(Properties properties) {
		EventSubscribeEntity entity = new EventSubscribeEntity();
		doBaseAnalyze(properties, entity);
		
		return entity;
	}
	
	/**
	 * 解析关注/取消关注-取消订阅事件文本消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doEventUnSubscribeAnalyze(Properties properties) {
		EventUnSubscribeEntity entity = new EventUnSubscribeEntity();
		doBaseAnalyze(properties, entity);
		
		return entity;
	}

	private void doEventScanSubscribeAnalyze(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	private void doEventScanAnalyze(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	private void doEventLocationAnalyze(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	private void doEventClickAnalyze(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	private void doEventViewAnalyze(Properties properties) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 解析普通消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体对象
	 */
	private AbstractBaseEntity doMessageAnalyze(Properties properties) {
		String msgType = properties.getProperty(AbstractBaseEntity.MSG_TYPE);
		if (msgType.equals(EntityEnum.TEXT.getMsgType())) {
			return doTextAnalyze(properties);
		}
		if (msgType.equals(EntityEnum.IMAGE.getMsgType())) {
			return doImageAnalyze(properties);
		}
		if (msgType.equals(EntityEnum.VOICE.getMsgType())) {
			return doVoiceAnalyze(properties);
		}
		if (msgType.equals(EntityEnum.VIDEO.getMsgType())) {
			return doVideoAnalyze(properties);
		}
		if (msgType.equals(EntityEnum.SHORTVIDEO.getMsgType())) {
			return doShortVideoAnalyze(properties);
		}
		if (msgType.equals(EntityEnum.LOCATION.getMsgType())) {
			return doLocationAnalyze(properties);
		}		
		if (msgType.equals(EntityEnum.LINK.getMsgType())) {
			return doLinkAnalyze(properties);
		}
		throw new IllegalStateException(String.format("解析普通消息出错:(%s)消息类型未知", msgType));
	}
	
	/**
	 * 基础解析
	 * @param properties POST推送数据解析后的properties
	 * @param entity 用于基础解析的实体
	 */
	private void doBaseAnalyze(Properties properties, AbstractBaseEntity entity) {
		String toUserName = properties.getProperty(TextEntity.TO_USER_NAME);
		Assert.hasText(toUserName);
		String fromUserName = properties.getProperty(TextEntity.FORM_USER_NAME);
		Assert.hasText(fromUserName);
		String createTimeStr = properties.getProperty(TextEntity.CREATE_TIME);
		Assert.hasText(createTimeStr);
		// 将时间整形转换为对象
		Date createTime = new Date(Long.parseLong(createTimeStr));
		Assert.notNull(createTime);
		
		entity.setToUserName(toUserName);
		entity.setFromUserName(fromUserName);
		entity.setCreateTime(createTime);
	}

	/**
	 * 解析普通文本消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doTextAnalyze(Properties properties) {
		/* 示例
	 	 <xml>
	     <ToUserName><![CDATA[toUser]]></ToUserName>
	 	 <FromUserName><![CDATA[fromUser]]></FromUserName> 
	 	 <CreateTime>1348831860</CreateTime>
	 	 <MsgType><![CDATA[text]]></MsgType>
	 	 <Content><![CDATA[this is a test]]></Content>
	 	 <MsgId>1234567890123456</MsgId>
	 	 </xml>
	 	 */ 
		TextEntity entity = new TextEntity();
		doBaseAnalyze(properties, entity);
		String content = properties.getProperty(TextEntity.CONTENT);
		Assert.hasText(content);
		String msgId = properties.getProperty(TextEntity.MSG_ID);
		Assert.hasText(msgId);		

		entity.setContent(content);
		entity.setMsgId(msgId);
		
		return entity;
	}
	
	/**
	 * 解析普通图片消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doImageAnalyze(Properties properties) {
		/* 示例
		 <xml>
 		 <ToUserName><![CDATA[toUser]]></ToUserName>
 		 <FromUserName><![CDATA[fromUser]]></FromUserName>
 		 <CreateTime>1348831860</CreateTime>
 		 <MsgType><![CDATA[image]]></MsgType>
 		 <PicUrl><![CDATA[this is a url]]></PicUrl>
 		 <MediaId><![CDATA[media_id]]></MediaId>
 		 <MsgId>1234567890123456</MsgId>
 		 </xml>
		 */
		ImageEntity entity = new ImageEntity();
		doBaseAnalyze(properties, entity);
		String picUrl = properties.getProperty(ImageEntity.PIC_URL);
		Assert.hasText(picUrl);
		String mediaId = properties.getProperty(ImageEntity.MEDIA_ID);
		Assert.hasText(mediaId);
		String msgId = properties.getProperty(TextEntity.MSG_ID);
		Assert.hasText(msgId);				

		entity.setPicUrl(picUrl);
		entity.setMediaId(mediaId);
		entity.setMsgId(msgId);
		
		return entity;
	}

	/**
	 * 解析普通语音消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doVoiceAnalyze(Properties properties) {
		/* 示例
		 <xml>
		 <ToUserName><![CDATA[toUser]]></ToUserName>
		 <FromUserName><![CDATA[fromUser]]></FromUserName>
		 <CreateTime>1357290913</CreateTime>
		 <MsgType><![CDATA[voice]]></MsgType>
		 <MediaId><![CDATA[media_id]]></MediaId>
		 <Format><![CDATA[Format]]></Format>
		 <MsgId>1234567890123456</MsgId>
		 </xml>
		 */
		VoiceEntity entity = new VoiceEntity();
		doBaseAnalyze(properties, entity);
		String mediaId = properties.getProperty(VoiceEntity.MEDIA_ID);
		Assert.hasText(mediaId);
		String format = properties.getProperty(VoiceEntity.FORMAT);
		Assert.hasText(format);
		String msgId = properties.getProperty(TextEntity.MSG_ID);
		Assert.hasText(msgId);				
		
		entity.setMediaId(mediaId);
		entity.setFormat(format);
		entity.setMsgId(msgId);
		
		return entity;
	}	
	
	/**
	 * 解析普通视频消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doVideoAnalyze(Properties properties) {
		/* 示例
		 <xml>
		 <ToUserName><![CDATA[toUser]]></ToUserName>
		 <FromUserName><![CDATA[fromUser]]></FromUserName>
		 <CreateTime>1357290913</CreateTime>
		 <MsgType><![CDATA[video]]></MsgType>
		 <MediaId><![CDATA[media_id]]></MediaId>
		 <ThumbMediaId><![CDATA[thumb_media_id]]></ThumbMediaId>
		 <MsgId>1234567890123456</MsgId>
		 </xml>
		 */
		VideoEntity entity = new VideoEntity();
		doBaseAnalyze(properties, entity);
		String mediaId = properties.getProperty(VideoEntity.MEDIA_ID);
		Assert.hasText(mediaId);
		String thumbMediaId = properties.getProperty(VideoEntity.THUMB_MEDIA_ID);
		Assert.hasText(thumbMediaId);
		String msgId = properties.getProperty(VideoEntity.MSG_ID);
		Assert.hasText(msgId);				
		
		entity.setMediaId(mediaId);
		entity.setThumbMediaId(thumbMediaId);
		entity.setMsgId(msgId);
		
		return entity;
	}

	/**
	 * 解析普通小视频消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doShortVideoAnalyze(Properties properties) {
		/* 示例
		 <xml>
		 <ToUserName><![CDATA[toUser]]></ToUserName>
		 <FromUserName><![CDATA[fromUser]]></FromUserName>
		 <CreateTime>1357290913</CreateTime>
		 <MsgType><![CDATA[shortvideo]]></MsgType>
		 <MediaId><![CDATA[media_id]]></MediaId>
		 <ThumbMediaId><![CDATA[thumb_media_id]]></ThumbMediaId>
		 <MsgId>1234567890123456</MsgId>
		 </xml>
		 */
		ShortVideoEntity entity = new ShortVideoEntity();
		doBaseAnalyze(properties, entity);
		String mediaId = properties.getProperty(VideoEntity.MEDIA_ID);
		Assert.hasText(mediaId);
		String thumbMediaId = properties.getProperty(VideoEntity.THUMB_MEDIA_ID);
		Assert.hasText(thumbMediaId);
		String msgId = properties.getProperty(VideoEntity.MSG_ID);
		Assert.hasText(msgId);				
		
		entity.setMediaId(mediaId);
		entity.setThumbMediaId(thumbMediaId);
		entity.setMsgId(msgId);
		
		return entity;
	}	

	/**
	 * 解析普通位置消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doLocationAnalyze(Properties properties) {
		/* 示例
		 <xml>
		 <ToUserName><![CDATA[toUser]]></ToUserName>
		 <FromUserName><![CDATA[fromUser]]></FromUserName>
	  	 <CreateTime>1351776360</CreateTime>
		 <MsgType><![CDATA[location]]></MsgType>
		 <Location_X>23.134521</Location_X>
		 <Location_Y>113.358803</Location_Y>
		 <Scale>20</Scale>
		 <Label><![CDATA[位置信息]]></Label>
		 <MsgId>1234567890123456</MsgId>
		 </xml> 		
		 */
		LocationEntity entity = new LocationEntity();
		doBaseAnalyze(properties, entity);
		String locationX = properties.getProperty(LocationEntity.LOCATION_X);
		Assert.hasText(locationX);
		String locationY = properties.getProperty(LocationEntity.LOCATION_Y);
		Assert.hasText(locationY);
		String scale = properties.getProperty(LocationEntity.SCALE);
		Assert.hasText(scale);
		String label = properties.getProperty(LocationEntity.LABEL);
		Assert.hasText(label);
		String msgId = properties.getProperty(TextEntity.MSG_ID);
		Assert.hasText(msgId);
		
		entity.setLocationX(locationX);
		entity.setLocationY(locationY);
		entity.setScale(scale);
		entity.setLabel(label);
		entity.setMsgId(msgId);
		
		return entity;
	}

	/**
	 * 解析普通链接消息
	 * @param properties POST推送数据解析后的properties
	 * @return 解析后的实体
	 */
	private AbstractBaseEntity doLinkAnalyze(Properties properties) {
		/*
		 <xml>
		 <ToUserName><![CDATA[toUser]]></ToUserName>
		 <FromUserName><![CDATA[fromUser]]></FromUserName>
		 <CreateTime>1351776360</CreateTime>
		 <MsgType><![CDATA[link]]></MsgType>
		 <Title><![CDATA[公众平台官网链接]]></Title>
		 <Description><![CDATA[公众平台官网链接]]></Description>
		 <Url><![CDATA[url]]></Url>
		 <MsgId>1234567890123456</MsgId>
		 </xml> 
		 */
		LinkEntity entity = new LinkEntity();
		doBaseAnalyze(properties, entity);
		String title = properties.getProperty(LinkEntity.TITLE);
		Assert.hasText(title);
		String description = properties.getProperty(LinkEntity.DESCRIPTION);
		Assert.hasText(description);
		String url = properties.getProperty(LinkEntity.URL);
		Assert.hasText(url);
		String msgId = properties.getProperty(TextEntity.MSG_ID);
		Assert.hasText(msgId);
		
		entity.setTitle(title);
		entity.setDescription(description);
		entity.setUrl(url);
		entity.setMsgId(msgId);
		
		return entity;
	}

	@Override
	String process(AbstractBaseEntity entity) {
		return null;
	}

	/**
	 * 取得信息处理器
	 * @return 处理器
	 */
	public IPostProcessor getProcessor() {
		return processor;
	}

	/**
	 * 设置信息处理器
	 * @param processor 处理器
	 */
	public void setProcessor(IPostProcessor processor) {
		this.processor = processor;
	}

}
