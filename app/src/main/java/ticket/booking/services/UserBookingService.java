package ticket.booking.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class UserBookingService{

    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;



    private User user;

    private final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";



    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();

    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();

    }

    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }




    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }

    // todo: Complete this function
    public Boolean cancelBooking(String ticketId)  {

            List<Ticket> bookedTickets = user.getTicketsBooked();
            Optional<Ticket> ticket = bookedTickets.stream().filter(ticket1 -> ticket1.getTicketId().equalsIgnoreCase(ticketId)).findFirst();

            if(ticket.isPresent()){
                bookedTickets.remove(ticket.get());
                return true;
            } else {
                return false;
            }

//            other method to cancel ticket more clean
//        return bookedTickets.removeIf(ticket -> ticket.getTicketId().equalsIgnoreCase(ticketId));

    }


    public List<Train> getTrains(String source, String destination){
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
           return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat, String source, String destination) {
        try {
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    // Book the seat
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);

                    TrainService trainService = new TrainService();
                    trainService.addTrain(train);

                    String dateOfTravel = LocalDate.now().toString();
                    Ticket newTicket = new Ticket(UUID.randomUUID().toString(), user.getUserId(), source, destination, dateOfTravel, train);

                    for (User u : userList) {
                        if (u.getUserId().equals(user.getUserId())) {
                            List<Ticket> userTickets = u.getTicketsBooked();
                            if (userTickets == null) {
                                userTickets = new ArrayList<>();
                            }
                            userTickets.add(newTicket);
                            u.setTicketsBooked(userTickets); // ✅ update the list in memory
                            break;
                        }
                    }
                    saveUserListToFile();
                    return true;
                } else {
                    return false; // Already booked
                }
            } else {
                return false; // Invalid index
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }


}