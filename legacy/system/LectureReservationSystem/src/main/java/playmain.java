/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author jun
 */
import Controller.ReservationController;
import View.ReservationView;
import Controller.UserController;
import View.UserView;
import java.util.Scanner;

public class playmain {
    public static void main(String[] args) {
        UserController userController = new UserController();
        UserView userView = new UserView(userController);
        userView.start();
        
        // ReservationController 객체 생성
        ReservationController reservationController = new ReservationController();

        // ReservationView 객체 생성
        ReservationView reservationView = new ReservationView(reservationController);
        
        // Scanner 객체 생성
        Scanner scanner = new Scanner(System.in);

        // ReservationView 실행 시 Scanner 객체 전달
        reservationView.start(scanner); 
        
        
    }
}
