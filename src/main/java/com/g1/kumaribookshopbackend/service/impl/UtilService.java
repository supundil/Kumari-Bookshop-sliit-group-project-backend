package com.g1.kumaribookshopbackend.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.g1.kumaribookshopbackend.repository.AdminRepository;
import com.g1.kumaribookshopbackend.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static com.g1.kumaribookshopbackend.util.AppConstant.ADMIN_ROLE;
import static com.g1.kumaribookshopbackend.util.AppConstant.USER_ROLE;

@Service
@Slf4j
public class UtilService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Value("${spring.jwt.secret}")
    private String secret;

    protected String hidePassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    protected Boolean checkPassword(String hiddenPassword, String password){
        return BCrypt.checkpw(password, hiddenPassword);
    }

    protected byte[] compressImage(byte[] data){
        try {
            Deflater deflater = new Deflater();
            deflater.setLevel(Deflater.BEST_COMPRESSION);
            deflater.setInput(data);
            deflater.finish();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] tmp = new byte[4*1024];
            while (!deflater.finished()) {
                int size = deflater.deflate(tmp);
                outputStream.write(tmp, 0, size);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            return ByteBuffer.allocate(4).putInt(0).array();
        }
    }


    protected byte[] decompressImage(byte[] data){
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] tmp = new byte[4*1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            return ByteBuffer.allocate(4).putInt(0).array();
        }
    }

    protected String getUserTaken(String username, String password){
        Date expireDate = new Date();
        Calendar cal= Calendar.getInstance();
        cal.setTime(expireDate);
        cal.add(Calendar.DATE, 1);
        expireDate= cal.getTime();


        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create().withIssuer("bloom-biz")
                .withClaim("username", username)
                .withClaim("password", password)
                .withExpiresAt(expireDate)
                .sign(algorithm);
    }

    public boolean requestAuthentication(String token, String role){

        Algorithm algorithm = Algorithm.HMAC256(secret);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("bloom-biz")
                .build();

        DecodedJWT decodedToken = verifier.verify(token);
        String username = decodedToken.getClaims().get("username").asString();

        if (role.equals(USER_ROLE)){
            return customerRepository.existsByUserName(username);
        } else if (role.equals(ADMIN_ROLE)){
            return adminRepository.existsByUserName(username);
        }
        return false;
    }

}
