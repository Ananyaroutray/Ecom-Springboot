package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.AddressRequestDto;
import com.java.ecom.dto.response.AddressResponseDto;
import com.java.ecom.entity.Address;
import com.java.ecom.entity.User;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.mapper.AddressMapper;
import com.java.ecom.repository.AddressRepo;
import com.java.ecom.repository.UserRepo;
import com.java.ecom.service.AddressService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepo userRepo;
    private final AddressMapper addressMapper;

    @Override
    public List<AddressResponseDto> getAddressesByUser(Integer userId) {
        User user = userRepo.findById(userId).orElseThrow(()->new NotFoundException("User not found"));
        return user.getAddress().stream().map(addressMapper::toResponseDto).toList();
    }

    //baad m padhna h
    @Override
    @Transactional
    public AddressResponseDto addAddress(Integer userId, AddressRequestDto dto) {
        User user = userRepo.findById(userId).orElseThrow(()->new NotFoundException("user Not found"));
        Address address = addressMapper.toEntity(dto);
        if(user.getAddress().isEmpty()){
            address.setIsDefault(true);
        }
        if(Boolean.TRUE.equals(dto.getIsDefault())){
            unSetExistingDefault(user);
            address.setIsDefault(true);
        }
        user.getAddress().add(address);
        userRepo.save(user);
        return addressMapper.toResponseDto(address);
    }

    private void unSetExistingDefault(User user) {
        user.getAddress().forEach(addr->addr.setIsDefault(false));
    }

    //baad m
    @Override
    @Transactional
    public AddressResponseDto updateAddress(Integer userId, Integer addressId, AddressRequestDto dto) {
        User user = userRepo.findById(userId).orElseThrow(()->new NotFoundException("User not found for userId: "+ userId));
        Address address = user.getAddress().stream().filter(a-> false).findFirst().orElseThrow(()->new NotFoundException("Address not found"));
        addressMapper.updateEntityFromDto(dto,address);

        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            unSetExistingDefault(user);
            address.setIsDefault(true);
        }
        return addressMapper.toResponseDto(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Integer userId, Long addressId) {
        User user = userRepo.findById(userId).orElseThrow(()->new NotFoundException("User not found"));
        Address address = user.getAddress().stream().filter(a->a.getId().equals(addressId)).findFirst().orElseThrow(()->new NotFoundException("Address Not found"));
        if(!user.getAddress().contains(address)){
            throw new RuntimeException("Address does not belong to this user");
        }
        user.getAddress().remove(address);
    }
}
