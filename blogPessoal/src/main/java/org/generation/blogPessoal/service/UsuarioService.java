package org.generation.blogPessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;

import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

	private @Autowired UsuarioRepository repository;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		Optional<Usuario> usuarioExistente = repository.findByUsuario(usuario.getUsuario());
		if (usuarioExistente.isPresent()) {
			return Optional.empty();
		} else {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			String senhaEncoder = encoder.encode(usuario.getSenha());
			usuario.setSenha(senhaEncoder);

			return Optional.ofNullable(repository.save(usuario));
		}
	}

	public Optional<UserLogin> Logar(Optional<UserLogin> user) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = repository.findByUsuario(user.get().getUsuario());

		if (encoder.matches(user.get().getSenha(), usuario.get().getSenha())) {
			String auth = user.get().getUsuario() + ":" + user.get().getSenha();
			byte[] encodeAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
			String authHeader = "Basic " + new String(encodeAuth);

			user.get().setToken(authHeader);
			user.get().setNome(usuario.get().getNome());

			return user;
		}

		return null;
	}

}
