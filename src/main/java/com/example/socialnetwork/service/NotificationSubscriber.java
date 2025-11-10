//package com.example.socialnetwork.service;
//
//import com.example.socialnetwork.dto.NotificationDTO;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nimbusds.jose.shaded.gson.Gson;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationSubscriber {
//    private final SimpMessagingTemplate simpMessagingTemplate;
//    private final ObjectMapper objectMapper;
//    /**
//     * Lắng nghe sự kiện (event) từ Redis khi có message mới được publish.
//     * Method này được gọi tự động bởi RedisMessageListenerContainer (đã cấu hình trong RedisConfig)
//     * mỗi khi có dữ liệu được gửi lên channel "notifications".
//     */
//    @EventListener
//    public void handleNotification(String messageJson){
//        try {
//            // Chuyển JSON -> NotificationDTO bằng Jackson
//            NotificationDTO notificationDTO = objectMapper.readValue(messageJson.trim(), NotificationDTO.class);
//
//            // Gửi đến topic tương ứng với user nhận
//            String destination = "/topic/notifications-" + notificationDTO.getReceiverId();
//            simpMessagingTemplate.convertAndSend(destination, notificationDTO);
//
//        } catch (Exception e) {
//            e.printStackTrace(); // Có thể log.error thay cho debug
//        }
//    }
//
//}
