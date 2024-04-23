package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Requests;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RequestRepository;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository= requestRepository;
    }

    public List<Requests> getSentRequests(Long user) {
        return requestRepository.getSentRequests(user);
    }

    public List<Requests> getReceivedRequests(Long user) {
        return requestRepository.getReceivedRequests(user);
    }

    public void save(Requests requestEntity) {
        requestRepository.save(requestEntity);
    }

    public void delete(Requests requestEntity) {
        requestRepository.delete(requestEntity);
    }

    public Optional<Requests> getRequest(Long user1, Long user2) {
        return requestRepository.getRequest(user1, user2);
    }

    
}
