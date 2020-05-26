package com.vishnu.mainfile;

import com.vishnu.ws.ControllerString;
import com.vishnu.ws.DaoString;
import com.vishnu.ws.ServiceString;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Vishnu
 */
public class WS_Generator {

    public static final String controllerfolderlocation = "D:\\Bizmobia Projects\\Parlour\\Generated\\controller\\";
    public static final String servicesfolderlocation = "D:\\Bizmobia Projects\\Parlour\\Generated\\service\\";
    public static final String servicesImplfolderlocation = "D:\\Bizmobia Projects\\Parlour\\Generated\\serviceImpl\\";
    public static final String daofolderlocation = "D:\\Bizmobia Projects\\Parlour\\Generated\\dao\\";

    public static void main(String[] args) {
        System.out.println(generateWS());
    }

    public static String generateWS() {
        String returnvalue = "";
        FileWriter wrtr;
        PrintWriter pntr;
        File file;

        List<String> strings = ModelList.modelList();
        for (int i = 0; i < strings.size(); i++) {
            //controller
            try {
                file = new File(controllerfolderlocation + strings.get(i).replaceAll("\\s", "") + "Controller.java");
                file.createNewFile();
                wrtr = new FileWriter(file, true);
                pntr = new PrintWriter(wrtr);
                pntr.append(ControllerString.getControllerString(strings.get(i).replaceAll("\\s", ""), strings.get(i).replaceAll("\\s", "").toLowerCase()));
                pntr.close();
                returnvalue = returnvalue + strings.get(i).replaceAll("\\s", "") + "'s Controller file created successfully,\n";
            } catch (IOException e) {
                returnvalue = returnvalue + "Exception while creating " + strings.get(i).replaceAll("\\s", "") + "'s Controller file i.e., " + e.getMessage() + ",\n";
            }
            //service interface
            try {
                file = new File(servicesfolderlocation + strings.get(i).replaceAll("\\s", "") + "Service.java");
                file.createNewFile();
                wrtr = new FileWriter(file, true);
                pntr = new PrintWriter(wrtr);
                pntr.append(ServiceString.getServiceInterfaceString(strings.get(i).replaceAll("\\s", "")));
                pntr.close();
                returnvalue = returnvalue + strings.get(i).replaceAll("\\s", "") + "'s Service interface file created successfully,\n";
            } catch (IOException e) {
                returnvalue = returnvalue + "Exception while creating " + strings.get(i).replaceAll("\\s", "") + "'s Service interface file i.e., " + e.getMessage() + ",\n";
            }
            //service impl
            try {
                file = new File(servicesImplfolderlocation + strings.get(i).replaceAll("\\s", "") + "ServiceImpl.java");
                file.createNewFile();
                wrtr = new FileWriter(file, true);
                pntr = new PrintWriter(wrtr);
                pntr.append(ServiceString.getServiceImplString(strings.get(i).replaceAll("\\s", ""), strings.get(i).replaceAll("\\s", "").toLowerCase()));
                pntr.close();
                returnvalue = returnvalue + strings.get(i).replaceAll("\\s", "") + "'s Service implementation file created successfully,\n";
            } catch (IOException e) {
                returnvalue = returnvalue + "Exception while creating " + strings.get(i).replaceAll("\\s", "") + "'s Service implematetion file i.e., " + e.getMessage() + ",\n";
            }
            //dao interface
            try {
                file = new File(daofolderlocation + strings.get(i).replaceAll("\\s", "") + "Dao.java");
                file.createNewFile();
                wrtr = new FileWriter(file, true);
                pntr = new PrintWriter(wrtr);
                pntr.append(DaoString.getDAOString(strings.get(i).replaceAll("\\s", "")));
                returnvalue = returnvalue + strings.get(i).replaceAll("\\s", "") + "'s DAO interface file created successfully,\n";
                pntr.close();
            } catch (IOException e) {
                returnvalue = returnvalue + "Exception while creating " + strings.get(i).replaceAll("\\s", "") + "'s DAO interface file i.e., " + e.getMessage() + ",\n";
            }
        }
        return returnvalue;
    }

}
