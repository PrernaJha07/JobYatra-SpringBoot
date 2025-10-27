package com.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendApplicationStatusEmail(String toEmail, String userName, String jobTitle, String company, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, "CareerConnect 🚀");
            helper.setTo(toEmail);
            helper.setSubject("🎯 " + getStatusEmoji(status) + " Application Update: " + jobTitle + " at " + company);
            
            String htmlContent = buildEmailContent(userName, jobTitle, company, status, toEmail);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("✅ Beautiful email sent to: " + toEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            return false;
        }
    }

    private String getStatusEmoji(String status) {
        switch (status.toLowerCase()) {
            case "success": return "🎉";
            case "rejected": return "📝";
            case "pending": return "⏳";
            default: return "📧";
        }
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "success": return "#10b981";
            case "rejected": return "#ef4444";
            case "pending": return "#f59e0b";
            default: return "#6366f1";
        }
    }

    private String getStatusMessage(String status) {
        switch (status.toLowerCase()) {
            case "success":
                return "Congratulations! Your application has been <strong>APPROVED</strong>. The company is impressed with your qualifications and would like to move forward.";
            case "rejected":
                return "We regret to inform you that your application has been <strong>REJECTED</strong>. Don't be discouraged - many factors influence hiring decisions.";
            case "pending":
                return "Your application is currently <strong>UNDER REVIEW</strong>. The hiring team is carefully evaluating all candidates.";
            default:
                return "There's an important update regarding your application.";
        }
    }

    private String getNextSteps(String status) {
        switch (status.toLowerCase()) {
            case "success":
                return "<li>📞 You will receive a call within 3-5 business days</li>" +
                       "<li>💼 Prepare for the interview process</li>" +
                       "<li>📚 Review the company's background and values</li>" +
                       "<li>🎯 Update your portfolio and references</li>";
            case "rejected":
                return "<li>🔍 Continue exploring other opportunities</li>" +
                       "<li>📈 Request feedback to improve future applications</li>" +
                       "<li>🎯 Consider similar positions in our job portal</li>" +
                       "<li>💪 Stay positive - the right opportunity is coming</li>";
            case "pending":
                return "<li>⏰ Please be patient during the review process</li>" +
                       "<li>📧 Ensure your contact information is up-to-date</li>" +
                       "<li>📱 Keep your phone handy for potential calls</li>" +
                       "<li>🔔 Check your email regularly for updates</li>";
            default:
                return "<li>📧 Monitor your email for further communications</li>" +
                       "<li>📱 Keep your profile updated</li>" +
                       "<li>💼 Continue building your skills</li>";
        }
    }

    private String buildEmailContent(String userName, String jobTitle, String company, String status, String toEmail) {
        String statusColor = getStatusColor(status);
        String statusEmoji = getStatusEmoji(status);
        String statusMessage = getStatusMessage(status);
        String nextSteps = getNextSteps(status);
        
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>CareerConnect - Application Status</title>" +
               "    <style>" +
               "        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');" +
               "        * { margin: 0; padding: 0; box-sizing: border-box; }" +
               "        body { font-family: 'Inter', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 20px; }" +
               "        .email-container { max-width: 650px; margin: 0 auto; background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.1); }" +
               "        .header { background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%); color: white; padding: 40px 30px; text-align: center; position: relative; }" +
               "        .header::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: url('data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1000 1000\"><polygon fill=\"rgba(255,255,255,0.1)\" points=\"0,1000 1000,0 1000,1000\"/></svg>'); }" +
               "        .header-content { position: relative; z-index: 2; }" +
               "        .logo { font-size: 32px; font-weight: 700; margin-bottom: 10px; }" +
               "        .tagline { font-size: 16px; opacity: 0.9; margin-bottom: 20px; }" +
               "        .content { padding: 40px 30px; }" +
               "        .greeting { font-size: 24px; font-weight: 600; color: #1f2937; margin-bottom: 20px; }" +
               "        .application-card { background: #f8fafc; border-radius: 15px; padding: 25px; margin: 25px 0; border-left: 5px solid " + statusColor + "; }" +
               "        .status-badge { background: " + statusColor + "; color: white; padding: 10px 20px; border-radius: 25px; font-weight: 600; display: inline-block; margin: 10px 0; font-size: 14px; }" +
               "        .job-details { background: white; border: 2px solid #e5e7eb; border-radius: 12px; padding: 20px; margin: 20px 0; }" +
               "        .detail-row { display: flex; justify-content: space-between; margin: 10px 0; padding: 8px 0; border-bottom: 1px solid #f3f4f6; }" +
               "        .detail-label { font-weight: 500; color: #6b7280; }" +
               "        .detail-value { font-weight: 600; color: #1f2937; }" +
               "        .next-steps { background: linear-gradient(135deg, #f0f9ff, #e0f2fe); border-radius: 15px; padding: 25px; margin: 25px 0; }" +
               "        .steps-title { font-size: 18px; font-weight: 600; color: #0369a1; margin-bottom: 15px; display: flex; align-items: center; gap: 10px; }" +
               "        .steps-list { list-style: none; }" +
               "        .steps-list li { padding: 8px 0; margin: 5px 0; display: flex; align-items: center; gap: 10px; }" +
               "        .social-section { text-align: center; margin: 30px 0; padding: 25px; background: #f8fafc; border-radius: 15px; }" +
               "        .social-title { font-size: 16px; font-weight: 600; color: #6b7280; margin-bottom: 15px; }" +
               "        .social-icons { display: flex; justify-content: center; gap: 20px; margin: 15px 0; }" +
               "        .social-icon { width: 40px; height: 40px; background: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #6366f1; text-decoration: none; font-size: 18px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); transition: all 0.3s ease; }" +
               "        .social-icon:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.15); }" +
               "        .footer { background: #1f2937; color: white; padding: 30px; text-align: center; }" +
               "        .footer-links { display: flex; justify-content: center; gap: 25px; margin: 15px 0; }" +
               "        .footer-link { color: #d1d5db; text-decoration: none; font-size: 14px; }" +
               "        .footer-link:hover { color: white; }" +
               "        .contact-info { font-size: 12px; color: #9ca3af; margin-top: 15px; }" +
               "        .cta-button { display: inline-block; background: linear-gradient(135deg, #4361ee, #3a0ca3); color: white; padding: 14px 32px; text-decoration: none; border-radius: 50px; font-weight: 600; margin: 20px 0; box-shadow: 0 4px 15px rgba(67, 97, 238, 0.3); transition: all 0.3s ease; }" +
               "        .cta-button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(67, 97, 238, 0.4); }" +
               "        .highlight-box { background: linear-gradient(135deg, #fff3cd, #ffecb3); border: 2px solid #ffd54f; border-radius: 12px; padding: 20px; margin: 20px 0; }" +
               "        .emoji { font-size: 24px; margin-right: 8px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class=\"email-container\">" +
               "        <!-- Header -->" +
               "        <div class=\"header\">" +
               "            <div class=\"header-content\">" +
               "                <div class=\"logo\">🚀 J O BY A T R A</div>" +
               "                <div class=\"tagline\">Connecting Talent with Opportunity</div>" +
               "                <div style=\"font-size: 48px; margin: 20px 0;\">" + statusEmoji + "</div>" +
               "                <h1 style=\"margin: 10px 0; font-size: 28px;\">Application Status Update</h1>" +
               "            </div>" +
               "        </div>" +
               "        " +
               "        <!-- Main Content -->" +
               "        <div class=\"content\">" +
               "            <div class=\"greeting\">Namaste " + userName + ",</div>" +
               "            <p style=\"color: #6b7280; line-height: 1.6; margin-bottom: 20px;\">We have an important update regarding your job application. Here are the details:</p>" +
               "            " +
               "            <!-- Application Card -->" +
               "            <div class=\"application-card\">" +
               "                <div class=\"status-badge\">" + statusEmoji + " STATUS: " + status.toUpperCase() + "</div>" +
               "                <p style=\"color: #374151; line-height: 1.6; margin: 15px 0;\">" + statusMessage + "</p>" +
               "            </div>" +
               "            " +
               "            <!-- Job Details -->" +
               "            <div class=\"job-details\">" +
               "                <h3 style=\"color: #1f2937; margin-bottom: 20px; display: flex; align-items: center; gap: 10px;\">" +
               "                    <span class=\"emoji\">💼</span> Application Details" +
               "                </h3>" +
               "                <div class=\"detail-row\">" +
               "                    <span class=\"detail-label\">Position Applied</span>" +
               "                    <span class=\"detail-value\">" + jobTitle + "</span>" +
               "                </div>" +
               "                <div class=\"detail-row\">" +
               "                    <span class=\"detail-label\">Company</span>" +
               "                    <span class=\"detail-value\">" + company + "</span>" +
               "                </div>" +
               "                <div class=\"detail-row\">" +
               "                    <span class=\"detail-label\">Application ID</span>" +
               "                    <span class=\"detail-value\">CC" + System.currentTimeMillis() + "</span>" +
               "                </div>" +
               "                <div class=\"detail-row\">" +
               "                    <span class=\"detail-label\">Date Applied</span>" +
               "                    <span class=\"detail-value\">" + java.time.LocalDate.now().toString() + "</span>" +
               "                </div>" +
               "            </div>" +
               "            " +
               "            <!-- Next Steps -->" +
               "            <div class=\"next-steps\">" +
               "                <div class=\"steps-title\">" +
               "                    <span class=\"emoji\">🎯</span> Next Steps" +
               "                </div>" +
               "                <ul class=\"steps-list\">" + nextSteps + "</ul>" +
               "            </div>" +
               "            " +
               "            <!-- Highlight Box -->" +
               "            <div class=\"highlight-box\">" +
               "                <h4 style=\"color: #856404; margin-bottom: 10px; display: flex; align-items: center; gap: 10px;\">" +
               "                    <span class=\"emoji\">💡</span> Pro Tip" +
               "                </h4>" +
               "                <p style=\"color: #856404; margin: 0; line-height: 1.5;\">" +
               "                    Keep your CareerConnect profile updated with new skills and experiences to increase your chances with future applications!" +
               "                </p>" +
               "            </div>" +
               "            " +
               "            <!-- CTA Button -->" +
               "            <div style=\"text-align: center; margin: 30px 0;\">" +
               "                <a href=\"http://localhost:8080\" class=\"cta-button\">View Your Applications</a>" +
               "            </div>" +
               "            " +
               "            <!-- Social Media -->" +
               "            <div class=\"social-section\">" +
               "                <div class=\"social-title\">Stay Connected With Us</div>" +
               "                <div class=\"social-icons\">" +
               "                    <a href=\"#\" class=\"social-icon\">📘</a>" +
               "                    <a href=\"#\" class=\"social-icon\">🐦</a>" +
               "                    <a href=\"#\" class=\"social-icon\">📸</a>" +
               "                    <a href=\"#\" class=\"social-icon\">💼</a>" +
               "                    <a href=\"#\" class=\"social-icon\">📹</a>" +
               "                </div>" +
               "                <p style=\"color: #6b7280; font-size: 14px; margin: 10px 0;\">Follow us for job tips and opportunities</p>" +
               "            </div>" +
               "        </div>" +
               "        " +
               "        <!-- Footer -->" +
               "        <div class=\"footer\">" +
               "            <div style=\"font-size: 18px; font-weight: 600; margin-bottom: 15px;\">CareerConnect</div>" +
               "            <p style=\"color: #d1d5db; margin-bottom: 20px; line-height: 1.5;\">" +
               "                Transforming the way people find their dream careers through innovative technology and personalized support." +
               "            </p>" +
               "            <div class=\"footer-links\">" +
               "                <a href=\"#\" class=\"footer-link\">Privacy Policy</a>" +
               "                <a href=\"#\" class=\"footer-link\">Terms of Service</a>" +
               "                <a href=\"#\" class=\"footer-link\">Contact Support</a>" +
               "                <a href=\"#\" class=\"footer-link\">Unsubscribe</a>" +
               "            </div>" +
               "            <div class=\"contact-info\">" +
               "                <p>Jobyatra PVT.LTD. • 123 Innovation Drive, Tech City, TC 12345</p>" +
               "                <p>Email: abhishekrajbanshi999.com • Phone: +919065856693</p>" +
               "                <p style=\"margin-top: 10px;\">This email was sent to " + toEmail + "</p>" +
               "            </div>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }
}