package com.vishnu.utilities;

/**
 *
 * @author Vishnu
 */
public class Stamp {

//    @Autowired
//    private static RegisterDao registerDao;
//    public static boolean validTimestampForOtp(Otp otp) throws Exception {
//        ResponseModel response = new ResponseModel();
//        String timestamp = new java.sql.Timestamp(System.currentTimeMillis()).toString();
//        Thread.sleep((long) (Math.random() * 1000 * (2 * 60)));
////        Thread.sleep(5000);
//        System.out.printf("Is the timestamp %s valid at %s? %b\n", timestamp, new java.sql.Timestamp(System.currentTimeMillis()).toString(), Stamp.validTimestamp(timestamp));
//        otp.setOtpexpirytime(new java.sql.Timestamp(System.currentTimeMillis()));
//        Status st = new Status();
//        st.setIdstatus(3);
//        otp.setOtpstatus(st);
//        response = registerDao.updateOtps(otp);
//        if (response.getStatusCode() == 0) {
//            return Stamp.validTimestamp(timestamp);
//        } else {
//            return false;
//        }
//
//    }
//    public static boolean validTimestamp(String timestamp) {
//        Date ts = java.sql.Timestamp.valueOf(timestamp);
//        Calendar c = Calendar.getInstance();
////        c.add(Calendar.SECOND, -15);
//        c.add(Calendar.MINUTE, -2);
//        Date fifteenSecondsAgo = c.getTime();
//        return ts.after(fifteenSecondsAgo);
//    }
}
