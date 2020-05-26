package com.bizmobia.vgwallet.vgwapp.utilities;

import javax.mail.MessagingException;

/**
 *
 * @author BizMobia23
 */
public class MailSendingMsg {

    public static void sendMail(String tomail, String message) throws MessagingException {

        String content = "<html>\n"
                + "    <head>\n"
                + "        <link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">\n"
                + "    </head>\n"
                + "    <body style=\"background-color: #ffffff\">\n"
                + "  <table class=\"m_-1248683720590822411collapse\" id=\"m_-1248683720590822411header\" style=\"width:448px;background-color:#ffffff;padding:0;margin:0;line-height:1px;font-size:1px\" width=\"448\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#ffffff\" align=\"center\">\n"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td colspan=\"4\" style=\"height:24px;padding:0;margin:0;line-height:1px;font-size:1px\" class=\"m_-1248683720590822411logo_space\" height=\"24\"> &nbsp; </td>\n"
                + "</tr>\n"
                + "<tr align=\"right\">\n"
                + "<td class=\"m_-1248683720590822411margin\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" width=\"24\"></td>\n"
                + "<td class=\"m_-1248683720590822411margin\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" width=\"24\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td colspan=\"3\" style=\"height:24px;padding:0;margin:0;line-height:1px;font-size:1px\" class=\"m_-1248683720590822411logo_space\" height=\"24\"> <img style=\"display:block;margin:0;padding:0;display:block;border:none;outline:none\" src=\"https://ci6.googleusercontent.com/proxy/vRM6tsswbNefebvmq-ZJgGxXLIWXbSG3RSR8xYFDrikws2V3jbxNmCC1Caz7HxPDL2VFAH1yi7iazfyYU3jHIo7PmbiKg860oVZVhw1RqZhBpeB-H41T5oG_XWbcrfg12dXUVPSUZCQbZJJLagB-Ya8j2sGFmpV4ebjg9kW0C_WoOVRPiC78LsoP5BlrDz74PM3lvD0=s0-d-e1-ft#https://twitter.com/scribe/ibis?t=1&amp;cn=cGFzc3dvcmRfcmVzZXRfdjI%3D&amp;iid=24cb5dba27b940d096cf176d5b8dd6a7&amp;uid=602554865&amp;nid=248+20\" class=\"CToWUd\" width=\"1\" height=\"1\"> </td>\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table>"
                + "\n"
                + "<table class=\"m_-1248683720590822411collapse\" id=\"m_-1248683720590822411header\" style=\"width:448px;background-color:#ffffff;padding:0;margin:0;line-height:1px;font-size:1px\" width=\"448\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#ffffff\" align=\"center\">"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td class=\"m_-1248683720590822411h2\" style=\"padding:0;margin:0;line-height:1px;font-size:1px;font-family:'HelveticaNeue','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:24px;line-height:32px;font-weight:bold;color:#292f33;text-align:left;text-decoration:none\" align=\"left;\"> OTP VERIFICATION</td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" height=\"12\"></td>\n"
                + "</tr>"
                + "<tr>\n"
                + "<td class=\"m_-1248683720590822411body-text\" style=\"padding:0;margin:0;line-height:1px;font-size:1px;font-family:'HelveticaNeue','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:16px;line-height:20px;font-weight:400;color:#292f33;text-align:left;text-decoration:none\" align=\"left;\">"
                + message
                + "</td></tr>"
                + "</tbody>\n"
                + "</table> </td>\n"
                + "<td class=\"m_-1248683720590822411margin\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" width=\"24\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"line-height:1px;display:block;height:1px;background-color:#f5f8fa;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"1\"></td>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\">\n"
                + "<td style=\"line-height:1px;display:block;height:1px;background-color:#f5f8fa;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"1\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td class=\"m_-1248683720590822411edu-margins\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" width=\"24\"></td>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\">\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table> </td>\n"
                + "<td class=\"m_-1248683720590822411edu-margins\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" width=\"24\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td colspan=\"3\" class=\"m_-1248683720590822411edu-space\" style=\"padding:0;margin:0;line-height:1px;font-size:1px\" height=\"24\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"line-height:1px;display:block;height:1px;background-color:#f5f8fa;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"1\"></td>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\">\n"
                + "<td style=\"line-height:1px;display:block;height:1px;background-color:#f5f8fa;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"1\"></td>\n"
                + "</tr>\n"
                + "\n"
                + "</tbody>\n"
                + "</table>\n"
                + "\n"
                + "\n"
                + "<table class=\"m_-1248683720590822411collapse\" id=\"m_-1248683720590822411footer\" style=\"width:448px;background-color:#ffffff;padding:0;margin:0;line-height:1px;font-size:1px\" width=\"448\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">\n"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\"> <span class=\"m_-1248683720590822411small-copy\" style=\"font-family:'HelveticaNeue','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:12px;line-height:16px;font-weight:400;color:#8899a6;text-align:left;text-decoration:none\"><a href=\"#\">Query</a> &nbsp;|&nbsp;<a href=\"#\">www.Bizmobia.com</a>&nbsp;|&nbsp;<a href=\"#\">Contact Us Form</a></span></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"height:12px;line-height:1px;font-size:1px;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"12\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\"> <span class=\"m_-1248683720590822411small-copy\" style=\"font-family:'HelveticaNeue','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:12px;line-height:16px;font-weight:400;color:#8899a6;text-align:left;text-decoration:none\"> This email was meant for @Contact </span> </td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"height:6px;line-height:1px;font-size:1px;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"6\"></td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"padding:0;margin:0;line-height:1px;font-size:1px\" align=\"center\"> <span class=\"m_-1248683720590822411address\"> <a href=\"#m_-1248683720590822411_\" style=\"text-decoration:none;border-style:none;border:0;padding:0;margin:0;font-family:'HelveticaNeue','Helvetica Neue',Helvetica,Arial,sans-serif;color:#8899a6;font-size:12px;padding:0px;margin:0px;font-weight:normal;line-height:12px\">BizMobia:\n"
                + "#10-3-282/2 , 2nd Floor Humayun Nagar,Mehdipatnam,Hyderabad 500028,India.\n"
                + "Mobile: +91-9515164908</a> </span> </td>\n"
                + "</tr>\n"
                + "<tr>\n"
                + "<td style=\"height:72px;padding:0;margin:0;line-height:1px;font-size:1px\" height=\"72\"></td>\n"
                + "</tr>\n"
                + "</tbody>\n"
                + "</table>\n";
        
        String fromemailid = "vix@bizmobia.com";
        String fromPassword = "vixpro@123";
        MailSender.sendWithoutAttachmentMail(fromemailid, fromPassword, tomail, "Password for login", content);
    }

}
