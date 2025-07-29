package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong");
            return;
        }

        while (option != 7) {
            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Cancel my Booking");
            System.out.println("5. Search & Book Train");
            System.out.println("6. Exit the App");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup:");
                    String signupName = scanner.next();
                    System.out.println("Enter the password to signup:");
                    String signupPass = scanner.next();
                    User newUser = new User(signupName, signupPass, UserServiceUtil.hashPassword(signupPass), new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(newUser);
                    break;

                case 2:
                    System.out.println("Enter the username to login:");
                    String loginName = scanner.next();
                    System.out.println("Enter the password to login:");
                    String loginPass = scanner.next();
                    User loginUser = new User(loginName, loginPass, UserServiceUtil.hashPassword(loginPass), new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(loginUser);
                    } catch (IOException ex) {
                        System.out.println("Login failed.");
                        return;
                    }
                    break;

                case 3:
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    System.out.println("Enter the ticket ID to cancel:");
                    String ticketId = scanner.next();
                    if (userBookingService.cancelBooking(ticketId)) {
                        System.out.println("Ticket with ID " + ticketId + " has been canceled.");
                    } else {
                        System.out.println("No ticket found with ID " + ticketId);
                    }
                    break;

                case 5:
                    scanner.nextLine(); // clear buffer
                    System.out.println("Enter source station:");
                    String source = scanner.nextLine();

                    System.out.println("Enter destination station:");
                    String destination = scanner.nextLine();

                    List<Train> trains = userBookingService.getTrains(source, destination);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found for this route.");
                        break;
                    }

                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + ". Train ID: " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("   Station: " + entry.getKey() + " -> Time: " + entry.getValue());
                        }
                        index++;
                    }

                    System.out.println("Select a train by typing 1 to " + trains.size() + ":");
                    int trainChoice = scanner.nextInt();
                    if (trainChoice < 1 || trainChoice > trains.size()) {
                        System.out.println("Invalid train selection.");
                        break;
                    }

                    Train selectedTrain = trains.get(trainChoice - 1);

                    // Show available seats
                    System.out.println("Available seats (0 = available, 1 = booked):");
                    List<List<Integer>> seats = userBookingService.fetchSeats(selectedTrain);
                    for (int i = 0; i < seats.size(); i++) {
                        System.out.print("Row " + i + ": ");
                        for (int j = 0; j < seats.get(i).size(); j++) {
                            System.out.print(seats.get(i).get(j) + " ");
                        }
                        System.out.println();
                    }

                    // Input for seat selection
                    System.out.println("Enter row number:");
                    int row = scanner.nextInt();

                    System.out.println("Enter column number:");
                    int col = scanner.nextInt();

                    // Booking
                    System.out.println("Booking seat...");
                    try {
                        boolean booked = userBookingService.bookTrainSeat(selectedTrain, row, col, source, destination);
                        if (booked) {
                            System.out.println("✅ Seat booked successfully! Enjoy your journey.");
                        } else {
                            System.out.println("❌ Seat not available or booking failed.");
                        }
                    } catch (Exception e) {
                        System.out.println("🚨 Error while booking seat: " + e.getMessage());
                        e.printStackTrace(); // will show exact cause and line number
                    }

                    break;


                case 6:
                    System.out.println("Thank you for using the Train Booking System!");
                    option = 7;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }
}
