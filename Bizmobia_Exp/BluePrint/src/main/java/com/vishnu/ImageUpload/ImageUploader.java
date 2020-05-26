package com.vishnu.ImageUpload;

import com.bizmobia.vgwallet.request.ImageRequest;
import com.bizmobia.vgwallet.request.RequestModel;
import com.bizmobia.vgwallet.response.ResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vishnu.ObjectMapping.ObjectMapperUtility;
import com.vishnu.Security.EncryptionFile;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaibhav
 */
@Service
public class ImageUploader {

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public BufferedImage resizeImage(final Image image, int width, int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        //below three lines are for RenderingHints for better image quality at cost of higher processing time
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public ResponseModel instantImageUpload(String autho_token, RequestModel respObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(respObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, (respObj.getUserId().length()));
            if (decryptdId.equals(respObj.getUserId())) {
                ImageRequest imageRequest = objectMapperUtility.jsonToObject(gson.toJson(respObj.getReqObject()), ImageRequest.class);
                byte[] imageByteArray = Base64.decodeBase64(imageRequest.getValue());
                String extension;
                if (imageRequest.getFiletype().contains("jpeg")) {
                    extension = ".jpg";
                } else if (imageRequest.getFiletype().contains("png")) {
                    extension = ".png";
                } else {
                    extension = ".jpg";
                }
                String filename = RandomStringUtils.randomNumeric(8) + extension;
//                **************************************************************
                String imagePath = "PATH HERE" + filename;
                String imageUrl = "URL HERE" + filename;
//                **************************************************************
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageByteArray));

                File file = new File(imagePath);
                file.createNewFile();
                try (FileOutputStream imageOutFile = new FileOutputStream(file)) {
                    imageOutFile.write(imageByteArray);
                    imageOutFile.close();
                }
                statuResponse.setMessage("Image Uploaded");
                statuResponse.setStatusCode(0);
                statuResponse.setExtraVariable(imageUrl);
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(11);
        }
        return statuResponse;
    }

    public ResponseModel instantImageDelete(String autho_token, RequestModel respObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(respObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, (respObj.getUserId().length()));
            if (decryptdId.equals(respObj.getUserId())) {
//                **************************************************************
                String imagepath = "PATH HERE" + respObj.getExtraVariable().substring(respObj.getExtraVariable().lastIndexOf("/"), respObj.getExtraVariable().length());
//                **************************************************************
                File file = new File(imagepath);
                if (file.exists()) {
                    file.delete();
                    statuResponse.setMessage("Upload Image");
                    statuResponse.setStatusCode(0);
                } else {
                    statuResponse.setMessage("Image Not Found");
                    statuResponse.setStatusCode(1);
                }
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(11);
        }
        return statuResponse;
    }

    public String imageUpload(ImageRequest imageRequest) {
        String imageicon = imageRequest.getValue();
        String imageType = imageRequest.getFiletype();
        String imageext = "";
        BufferedImage tempPNG = null;
        BufferedImage img = null;
        String filename = RandomStringUtils.randomNumeric(8);

//        **********************************************************************
        String imagePath = "PATH HERE";
//        **********************************************************************
        String actualfilename = imageRequest.getFilename();
        imageext = actualfilename.substring(actualfilename.lastIndexOf("."), actualfilename.length());
        String cattegoryiconpath = imagePath + filename + imageext;

        File catfile = new File(cattegoryiconpath);
        byte[] imageByteArray = Base64.decodeBase64(imageicon);
        try {
            try (FileOutputStream imageOutFile = new FileOutputStream(catfile)) {
                imageOutFile.write(imageByteArray);
                imageOutFile.close();
            }
            img = ImageIO.read(catfile);

            int width = img.getWidth();
            int height = img.getHeight();

            System.out.println("*********************images**********************" + width + "****" + height);
            double aspectRatio = (double) img.getWidth(null) / (double) img.getHeight(null);
            tempPNG = resizeImage(img, width, (int) (height / aspectRatio));
            catfile = new File(imagePath + filename + "_New.png");
            ImageIO.write(tempPNG, "png", catfile);
            return "URL HERE" + filename + ".png";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}