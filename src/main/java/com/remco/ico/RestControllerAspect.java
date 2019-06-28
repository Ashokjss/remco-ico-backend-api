package com.remco.ico;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import javax.naming.AuthenticationException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.gson.Gson;
import com.remco.ico.DTO.StatusResponseDTO;
import com.remco.ico.model.AuthorizationInfo;
import com.remco.ico.model.RegisterInfo;
import com.remco.ico.repository.AuthorizationInfoRepository;
import com.remco.ico.repository.RegisterInfoRepository;

@Aspect
@Component
public class RestControllerAspect {

	private static final Logger LOG = LoggerFactory.getLogger(RestControllerAspect.class);

	static final long ONE_MINUTE_IN_MILLIS = 60000;// millisecs

	@Autowired
	private AuthorizationInfoRepository authorizationInfoRepository;

	@Autowired
	private RegisterInfoRepository remcoUserRegisterInfoRepository;

	@Autowired
	private Environment env;

	@Around("within(com.remco.ico.controller.UserActivitiesController) || within(com.remco.ico.controller.AdminActivitiesController)")
	//@Around("within(com.remco.ico.controller.UserActivitiesController)")
	public Object handleAccessToken(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
		try {
			Object[] args = proceedingJoinPoint.getArgs();

			MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
			Method method = methodSignature.getMethod();
			Annotation[][] parameterAnnotations = (Annotation[][]) method.getParameterAnnotations();
			assert args.length == parameterAnnotations.length;
			String authTokenValue = "";
			String ownerType = "";
			for (int argIndex = 0; argIndex < args.length; argIndex++) {
				for (Annotation annotation : parameterAnnotations[argIndex]) {
					if (!(annotation instanceof RequestHeader))
						continue;
					RequestHeader requestHeader = (RequestHeader) annotation;
					if ("authToken".equals(requestHeader.value())) {
						authTokenValue = (String) args[argIndex];
						LOG.info("authTokenValue Header" + " = " + args[argIndex]);
					}
					if ("ownerType".equals(requestHeader.value())) {
						ownerType = (String) args[argIndex];
						LOG.info(" ownerType.value " + " = " + args[argIndex]);
					}
				}
			}
			if (authTokenValue == null || authTokenValue.isEmpty()) {
				throw new AuthenticationException("Auth token wrong");
			} else {
				LOG.info(" Before validating authentication : authTokenValue : " + authTokenValue);
				LOG.info(" Before validating authentication : ownerType : " + ownerType);
//				if ("User".equalsIgnoreCase(ownerType) || "Merchant".equalsIgnoreCase(ownerType)
//						|| "Vendor".equalsIgnoreCase(ownerType) || "Admin".equalsIgnoreCase(ownerType)
//						|| "SuperAdmin".equalsIgnoreCase(ownerType)) {
					// If owner is user (user/merchant)
					LOG.info(" Inside user : ownerType : " + ownerType);
					// String enCryptedInputToken =
					// EncryptDecrypt.encrypt(authTokenValue);
					AuthorizationInfo authorizationInfo = authorizationInfoRepository.findByAuthToken(authTokenValue);
					if (authorizationInfo != null) {
						RegisterInfo remcoUserRegisterInfo = remcoUserRegisterInfoRepository
								.findRegisterInfoByEmailIdIgnoreCase(authorizationInfo.getEmailId());
						if (remcoUserRegisterInfo != null) {
							String dbAuthToken = authorizationInfo.getAuthToken();
							if (!dbAuthToken.equalsIgnoreCase(authTokenValue)) {
								throw new AuthenticationException("Session expired");
							}
							LOG.info("Token Status:::::"+ authorizationInfo.getStatus());
							if (authorizationInfo.getStatus().equalsIgnoreCase("InActive")
									|| authorizationInfo.getExpiryDate().before(new Date())) {
								LOG.info(" Inside Inactive Status:::::" + authorizationInfo.getCreatedDate() + "  " +authorizationInfo.getExpiryDate() + new Date());
								authorizationInfo.setStatus("InActive");
								authorizationInfoRepository.save(authorizationInfo);
								throw new AuthenticationException("Session expired");
							} else {
								authorizationInfo.setCreatedDate(new Date());
								Calendar date = Calendar.getInstance();
								long t = date.getTimeInMillis();
								Date afterAddingThreeMins = new Date(t + (20 * ONE_MINUTE_IN_MILLIS));
								authorizationInfo.setExpiryDate(afterAddingThreeMins);
								authorizationInfoRepository.save(authorizationInfo);
							}
						} else {
							throw new AuthenticationException("Not a authenticated user");
						}
					} else {
						throw new AuthenticationException("Invalid tokenOwner");
					}
//				} else {
//					throw new AuthenticationException("Invalid account holder type");
//				}
			}

			LOG.error("Authentication checking completed : ");
			Object obj = proceedingJoinPoint.proceed();
			LOG.error("AOP call end : " + obj);
			return obj;

		} catch (AuthenticationException e) {
			LOG.error("In Rest controller verifing auth token / owner type : ");
			LOG.error("In Rest controller AuthenticationException : " + e.getMessage());
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(e.getMessage());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			LOG.error("In Rest controller verifing auth token / owner type : ");
			LOG.error("In Rest controller Exception : " + e.getMessage());
			statusResponseDTO.setStatus(env.getProperty("failure"));
			statusResponseDTO.setMessage(e.getMessage());
			return new ResponseEntity<String>(new Gson().toJson(statusResponseDTO), HttpStatus.UNAUTHORIZED);

		}

	}

}
