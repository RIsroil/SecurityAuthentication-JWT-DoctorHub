package com.example.demo.disease.mapper;

import com.example.demo.disease.CurrencyType;
import com.example.demo.disease.DiseaseEntity;
import com.example.demo.disease.model.DiseaseView;
import com.example.demo.disease.model.DiseaseResponse; // For converting View to Response
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface DiseaseMapper {
    DiseaseMapper INSTANCE = Mappers.getMapper(DiseaseMapper.class);

    @Mapping(source = "branch.id", target = "branchId")
    @Mapping(source = "branch.branchName", target = "branchName")
    @Mapping(source = "entity", target = "price", qualifiedByName = "formatPriceWithCurrency")
    DiseaseView toView(DiseaseEntity entity);

    List<DiseaseView> toViewList(List<DiseaseEntity> entities);

    @Named("formatPriceWithCurrency")
    default String formatPriceWithCurrency(DiseaseEntity entity) {
        if (entity.getPrice() == null || entity.getCurrency() == null) {
            return null; // Or some default like "N/A"
        }
        String currencyIcon = switch (entity.getCurrency()) {
            case DOLLAR -> "$";
            case SUM -> "сум";
            case RUB -> "₽";
            default -> ""; // Should not happen if currency is mandatory
        };
        return entity.getPrice() + " " + currencyIcon;
    }

    // Helper to convert DiseaseView to DiseaseResponse
    // This is needed because the controller should return DiseaseResponse
    default DiseaseResponse toDiseaseResponse(DiseaseView view) {
        if (view == null) {
            return null;
        }
        return DiseaseResponse.builder()
                .id(view.getId())
                .branchId(view.getBranchId())
                .branchName(view.getBranchName())
                .diseaseName(view.getDiseaseName())
                .price(view.getPrice())
                .build();
    }

    default List<DiseaseResponse> toDiseaseResponseList(List<DiseaseView> views) {
        if (views == null) {
            return null;
        }
        return views.stream()
                .map(this::toDiseaseResponse)
                .collect(Collectors.toList());
    }
}
