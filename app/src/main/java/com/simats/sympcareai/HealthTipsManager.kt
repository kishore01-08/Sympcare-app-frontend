package com.simats.sympcareai

import java.util.Calendar

object HealthTipsManager {
    private val healthTips = listOf(
        "Stay hydrated! Drinking 8 glasses of water daily helps maintain energy and supports overall health.",
        "Aim for at least 30 minutes of moderate physical activity today to boost your mood and heart health.",
        "Prioritize sleep! Getting 7-9 hours of quality sleep regenerates your body and mind.",
        "Eat more fruits and vegetables. They are packed with essential vitamins, minerals, and fiber.",
        "Practice mindfulness or deep breathing for 5 minutes to reduce stress and improve focus.",
        "Limit processed sugars and opt for natural sweetness from fruits to keep your energy stable.",
        "Stretch regularly, especially if you sit for long periods, to improve circulation and flexibility.",
        "Wash your hands frequently to prevent the spread of germs and stay healthy.",
        "Take breaks from screens every 20 minutes to reduce eye strain and prevent headaches.",
        "Connect with a friend or loved one today; social connections are vital for mental well-being.",
        "Protect your skin! Wear sunscreen if you're going outside, even on cloudy days.",
        "Listen to your body. If you're tired, rest. Pushing through exhaustion can lead to burnout.",
        "Practice gratitude. Write down three things you are thankful for to improve your outlook.",
        "Choose whole grains over refined grains for better digestion and sustained energy.",
        "Limit caffeine intake, especially in the afternoon, to ensure a good night's sleep.",
        "Maintain good posture to prevent back pain and improve breathing.",
        "Smile! It releases endorphins that reduce stress and boost your immune system.",
        "Spend time in nature. Fresh air and green surroundings can lower blood pressure.",
        "Eat mindfully. Chew slowly and savor your food to prevent overeating.",
        "Schedule your annual check-up if you haven't already. Prevention is better than cure."
    )

    fun getTodayTip(): String {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return healthTips[dayOfYear % healthTips.size]
    }
}
