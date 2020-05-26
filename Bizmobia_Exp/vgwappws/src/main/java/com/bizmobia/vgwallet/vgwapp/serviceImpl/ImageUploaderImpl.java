package com.bizmobia.vgwallet.vgwapp.serviceImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.bizmobia.vgwallet.vgwapp.dao.CompanyConfigDao;
import com.bizmobia.vgwallet.vgwapp.dao.ProductDao;
import com.bizmobia.vgwallet.vgwapp.models.CompanyConfig;
import com.bizmobia.vgwallet.vgwapp.models.Product;
import com.bizmobia.vgwallet.vgwapp.request.ImageRequest;
import com.bizmobia.vgwallet.vgwapp.request.RequestModel;
import com.bizmobia.vgwallet.vgwapp.response.ResponseModel;
import com.bizmobia.vgwallet.vgwapp.service.EncryptionFile;
import com.bizmobia.vgwallet.vgwapp.service.ImageUploader;
import com.bizmobia.vgwallet.vgwapp.service.ObjectMapperUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Vaibhav
 */
@Service
public class ImageUploaderImpl implements ImageUploader {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploaderImpl.class);

    @Autowired
    CompanyConfigDao companyConfigDao;
    
    @Autowired
    ProductDao productDao;

    @Autowired
    private ObjectMapperUtility objectMapperUtility;

    @Autowired
    private EncryptionFile encryptionFile;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public String imageUpload(ImageRequest imageRequest) {
        String categoryiconurl = "";
        String imageicon = imageRequest.getValue();
        String imageType = imageRequest.getFiletype();
        String imageext = "";
        BufferedImage tempPNG = null;
        BufferedImage img = null;
        String filename = RandomStringUtils.randomNumeric(8);

        CompanyConfig cmp = (CompanyConfig) companyConfigDao.findAll().get(0);
        String imagepath = cmp.getAddressProofPath();//-------------------------------------->uses same folder for address proof and id proof
        String actualfilename = imageRequest.getFilename();
        imageext = actualfilename.substring(actualfilename.lastIndexOf("."), actualfilename.length());
        String cattegoryiconpath = imagepath + filename + imageext;

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
            catfile = new File(imagepath + filename + "_New.png");
            ImageIO.write(tempPNG, "png", catfile);
            return cmp.getAddressProofUrl() + filename + "_New.png";
        } catch (IOException e) {
            logger.error("ImageUploaderImpl.class", "imageUpload()", "Plese try after Sometime " + e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

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

    @Override
    public ResponseModel instantImageUpload(String autho_token, RequestModel respObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(respObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, (respObj.getUserId().length()));
            if (decryptdId.equals(respObj.getUserId())) {
                ImageRequest imageRequest = objectMapperUtility.jsonToObject(gson.toJson(respObj.getReqObject()), ImageRequest.class);
                byte[] imageByteArray = Base64.decodeBase64(imageRequest.getValue());
                CompanyConfig cc = (CompanyConfig) companyConfigDao.findAll().get(0);
                String extension;
                if (imageRequest.getFiletype().contains("jpeg")) {
                    extension = ".jpg";
                } else if (imageRequest.getFiletype().contains("png")) {
                    extension = ".png";
                } else {
                    extension = ".jpg";
                }
                String filename = RandomStringUtils.randomNumeric(8) + extension;
                String imagePath = cc.getIdProofPath() + filename;
                String imageUrl = cc.getIdProofUrl() + filename;
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
                logger.error("ImageUploaderImpl.class", "instantImageUpload()", "Image Uploaded");
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(10);
                logger.error("ImageUploaderImpl.class", "instantImageUpload()", "Invalid User Access");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(11);
            logger.error("ImageUploaderImpl.class", "instantImageUpload()", "Plese try after Sometime " + e.getMessage());
        }
        return statuResponse;
    }

    @Override
    public ResponseModel instantImageDelete(String autho_token, RequestModel respObj) {
        ResponseModel statuResponse = new ResponseModel();
        try {
            String gendynamikey = encryptionFile.reGenerateEncryptedKey(respObj.getUserId(), autho_token);
            String decryptdId = encryptionFile.decrypt(gendynamikey, objectMapperUtility.encGenKey, autho_token, (respObj.getUserId().length()));
            if (decryptdId.equals(respObj.getUserId())) {
                CompanyConfig cc = (CompanyConfig) companyConfigDao.findAll().get(0);
                String imagepath = cc.getIdProofPath() + respObj.getExtraVariable().substring(respObj.getExtraVariable().lastIndexOf("/") + 1, respObj.getExtraVariable().length());
                File file = new File(imagepath);
                Product prodObj = null;
                if (file.exists()) {
                    file.delete();
                    if (respObj.getReqObject() != null) {
                        prodObj = objectMapperUtility.jsonToObject(gson.toJson(respObj.getReqObject()), Product.class);
//                        for (int i = 0; i < prodObj.getProductImage().size(); i++) {
//                            if (prodObj.getProductImage().get(i).equals(respObj.getExtraVariable())) {
//                                prodObj.getProductImage().remove(i);
//                                prodObj.setProductImage(prodObj.getProductImage());
//                            }
//                        }
                        productDao.saveAndFlush(prodObj);
                    }
                    statuResponse.setMessage("Image Delete Successful");
                    statuResponse.setStatusCode(0);
                    logger.info("ImageUploaderImpl.class", "instantImageDelete()", "Upload Image");
                } else {
                    statuResponse.setMessage("Image Not Found");
                    statuResponse.setStatusCode(1);
                    logger.error("ImageUploaderImpl.class", "instantImageDelete()", "Image Not Found");
                }
            } else {
                statuResponse.setMessage("Invalid User Access");
                statuResponse.setStatusCode(10);
                logger.error("ImageUploaderImpl.class", "instantImageDelete()", "Invalid User Access");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statuResponse.setMessage("Plese try after Sometime " + e.getMessage());
            statuResponse.setStatusCode(11);
            logger.error("ImageUploaderImpl.class", "instantImageDelete()", "Plese try after Sometime " + e.getMessage());
        }
        return statuResponse;
    }

}
