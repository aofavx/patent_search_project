package com.blcultra.util;

import io.jsonwebtoken.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用认证工具类，采用RSA加密
 * @author sgy
 *
 */
public class JWTUtils {
	private static RSAPrivateKey priKey;
	private static RSAPublicKey pubKey;

	private static class SingletonHolder {
		private static final JWTUtils INSTANCE = new JWTUtils();
	}

	public synchronized static JWTUtils getInstance(String modulus, String privateExponent, String publicExponent) {
		if (priKey == null && pubKey == null) {
			priKey = RSAUtils.getPrivateKey(modulus, privateExponent);
			pubKey = RSAUtils.getPublicKey(modulus, publicExponent);
		}
		return SingletonHolder.INSTANCE;
	}

	public synchronized static void reload(String modulus, String privateExponent, String publicExponent) {
		priKey = RSAUtils.getPrivateKey(modulus, privateExponent);
		pubKey = RSAUtils.getPublicKey(modulus, publicExponent);
	}
	
	public synchronized static JWTUtils getInstance() {
		if (priKey == null && pubKey == null) {
			priKey = RSAUtils.getPrivateKey(RSAUtils.modulus, RSAUtils.private_exponent);
			pubKey = RSAUtils.getPublicKey(RSAUtils.modulus, RSAUtils.public_exponent);
		}
		return SingletonHolder.INSTANCE;
	}
	
	/**
	 * 获取Token
	 * @param uid 用户ID
	 * @param exp 失效时间，单位分钟
	 * @return
	 */
	public static String getToken(String uid, int exp) {
		long endTime = System.currentTimeMillis() + 1000 * exp;
		return Jwts.builder().setSubject(uid).setExpiration(new Date(endTime))
				.signWith(SignatureAlgorithm.RS512, priKey).compact();
	}

	/**
	 * 获取Token
	 * @param uid 用户ID
	 * @return
	 */
	public Map<String ,Object> getToken(String uid) {
		Map<String,Object> map = new HashMap<>();
		long endTime = System.currentTimeMillis() + 1000 * 60 * 7 *1440;//过期时间48小时:7天
		String token = Jwts.builder().setSubject(uid).setExpiration(new Date(endTime))
				.signWith(SignatureAlgorithm.RS512, priKey).compact();
		map.put("expires_in",endTime);
		map.put("token",token);
		return map;
	}

	/**
	 * 检查Token是否合法
	 * @param token
	 * @return JWTResult
	 */
	public JWTResult checkToken(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(token).getBody();
			String sub = claims.get("sub", String.class);
			return new JWTResult(true, sub,"校验通过", "200");

		} catch (ExpiredJwtException e) {
			// 在解析JWT字符串时，如果‘过期时间字段’已经早于当前时间，将会抛出ExpiredJwtException异常，说明本次请求已经失效
			return new JWTResult(false, null, "token已过期", "400");
		} catch (SignatureException e) {
			// 在解析JWT字符串时，如果密钥不正确，将会解析失败，抛出SignatureException异常，说明该JWT字符串是伪造的
			return new JWTResult(false, null, "非法请求", "400");
		} catch (Exception e) {
			return new JWTResult(false, null, "非法请求", "400");
		}
	}

	public static class JWTResult {
		private boolean status;
		private String uid;
		private String msg;
		private String code;
		
		public JWTResult() {
			super();
		}

		public JWTResult(boolean status, String uid, String msg, String code) {
			super();
			this.status = status;
			this.uid = uid;
			this.msg = msg;
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}

		public String getUid() {
			return uid;
		}

		public void setUid(String uid) {
			this.uid = uid;
		}
	}

	public static void main(String[] args) {
		Map<String, Object> token = getInstance().getToken("d1e975d5173043c2aa4a7c7c74f2045a");
		System.out.println(token);
	}

}
