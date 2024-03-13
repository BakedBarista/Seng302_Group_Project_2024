package nz.ac.canterbury.seng302.gardenersgrove.repository;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, ValidationGroups.FirstOrder.class, ValidationGroups.SecondOrder.class})
public interface ValidationSequence {
}
