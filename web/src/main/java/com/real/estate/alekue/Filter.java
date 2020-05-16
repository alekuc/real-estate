package com.real.estate.alekue;

import com.real.estate.alekue.dao.Apartment;
import com.real.estate.alekue.dao.Apartment_;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Filter {

    private BigDecimal areaFrom;
    private BigDecimal areaUntil;

    private BigDecimal numberOfRoomsFrom;
    private BigDecimal numberOfRoomsUntil;

    private BigDecimal floorFrom;
    private BigDecimal floorUntil;

    private BigDecimal totalBuildingFloorsFrom;
    private BigDecimal totalBuildingFloorsUntil;

    private BigDecimal yearFrom;
    private BigDecimal yearUntil;

    private List<String> districts = new ArrayList<>();

    public Specification<Apartment> toSpecification() {
        Specification<Apartment> rootSpec = (root, cq, cb) -> cb.and();
        if (isNotEmpty(areaFrom)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.ge(root.get(Apartment_.AREA), areaFrom));
        }
        if (isNotEmpty(areaUntil)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.le(root.get(Apartment_.AREA), areaUntil));
        }
        if (isNotEmpty(numberOfRoomsFrom)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.ge(root.get(Apartment_.NUMBER_OF_ROOMS), numberOfRoomsFrom.intValue()));
        }
        if (isNotEmpty(numberOfRoomsUntil)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.le(root.get(Apartment_.NUMBER_OF_ROOMS), numberOfRoomsUntil.intValue()));
        }
        if (isNotEmpty(floorFrom)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.ge(root.get(Apartment_.FLOOR), floorFrom.intValue()));
        }
        if (isNotEmpty(floorUntil)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.le(root.get(Apartment_.FLOOR), floorUntil.intValue()));
        }
        if (isNotEmpty(totalBuildingFloorsFrom)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.ge(root.get(Apartment_.TOTAL_BUILDING_FLOORS), totalBuildingFloorsFrom.intValue()));
        }
        if (isNotEmpty(totalBuildingFloorsUntil)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.le(root.get(Apartment_.TOTAL_BUILDING_FLOORS), totalBuildingFloorsUntil.intValue()));
        }
        if (isNotEmpty(yearFrom)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.ge(root.get(Apartment_.YEAR), yearFrom.intValue()));
        }
        if (isNotEmpty(yearUntil)) {
            rootSpec = rootSpec.and((root, cq, cb) -> cb.le(root.get(Apartment_.YEAR), yearUntil.intValue()));
        }
        if (!CollectionUtils.isEmpty(districts)) {
            rootSpec = rootSpec.and((root, cq, cb) -> root.get(Apartment_.district).in(districts));
        }
        return rootSpec;
    }

    private boolean isNotEmpty(BigDecimal bigDecimal) {
        return bigDecimal != null && bigDecimal.compareTo(BigDecimal.ZERO) > 0;
    }

}
