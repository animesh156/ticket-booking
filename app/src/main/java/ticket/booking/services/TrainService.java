package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDb/train.json";

    public TrainService() throws IOException {
       File trains = new File(TRAIN_DB_PATH);
       trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }

    public List<Train> searchTrains(String source, String destination) {
       return trainList.stream().filter(train -> validTrain(train, source, destination)).collect(Collectors.toList());
    }

    public void addTrain(Train newTrain) throws IOException {

        Optional<Train> existingTrain = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(newTrain.getTrainId())).findFirst();

       if(existingTrain.isPresent()) {
           updateTrain(newTrain);
       } else {
          trainList.add(newTrain);

          saveTrainListToFile();
       }
    }

    public void updateTrain(Train updatedTrain) throws IOException {
        Optional<Train> existingTrain = trainList.stream().filter(train -> train.getTrainId().equalsIgnoreCase(updatedTrain.getTrainId())).findFirst();

        if(existingTrain.isPresent()) {
            Train train = existingTrain.get();

            train.setTrainNo(updatedTrain.getTrainNo());
            train.setSeats(updatedTrain.getSeats());
            train.setStations(updatedTrain.getStations());
            train.setStationTimes(updatedTrain.getStationTimes());

            saveTrainListToFile();

        } else {
            addTrain(updatedTrain);
        }

    }

    private void saveTrainListToFile() throws IOException {
      File trainsFile = new File(TRAIN_DB_PATH);
      objectMapper.writeValue(trainsFile, trainList);
    }

    private boolean validTrain(Train train, String source, String destination) {
      List<String> stationOrder = train.getStations();

      int sourceInd = stationOrder.indexOf(source.toLowerCase());
      int destinationInd = stationOrder.indexOf(destination.toLowerCase());

      return sourceInd != -1 && destinationInd != -1 && sourceInd < destinationInd;
    }
}