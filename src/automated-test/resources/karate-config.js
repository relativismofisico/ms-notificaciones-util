function fn() {

    var env = karate.env || 'dev';
    karate.log('=== karate.env:', env, '===');

    // ── Configuración base por ambiente ──────────────────────────────────────
    var config = {
        env: env,
        baseUrl: 'http://localhost:8055/securityapi',
        jwtSecret: java.lang.System.getenv('JWT_SECRET') ||
                   'AF84F1FGllNpNnLG055fdg5hGHJK4KGG5VH5TR5J05JFGGDFDGXVV545J4505G666JFGF2mMY95y',
        cantidadLimiteDestinatarios: 50,
        productionMode: false
    };

    if (env === 'qa') {
        config.baseUrl = java.lang.System.getenv('QA_BASE_URL') || 'http://qa-server:8055/securityapi';
        config.jwtSecret = java.lang.System.getenv('JWT_SECRET') || config.jwtSecret;
    }

    if (env === 'prod') {
        config.baseUrl = java.lang.System.getenv('PROD_BASE_URL');
        config.jwtSecret = java.lang.System.getenv('JWT_SECRET');
        config.productionMode = true;
        if (!config.baseUrl) {
            karate.fail('PROD_BASE_URL es obligatoria en ambiente prod');
        }
        if (!config.jwtSecret) {
            karate.fail('JWT_SECRET es obligatoria en ambiente prod');
        }
    }

    // ── Generación de tokens JWT usando helper Java ───────────────────────────
    var JwtHelper = Java.type('co.com.karate.helper.JwtTestHelper');

    config.tokenAdmin       = JwtHelper.generateToken('qa-admin',     'ADMINISTRADOR', config.jwtSecret, 300);
    config.tokenEmpresa     = JwtHelper.generateToken('qa-empresa',   'EMPRESA',       config.jwtSecret, 300);
    config.tokenOperario    = JwtHelper.generateToken('qa-operario',  'OPERARIO',      config.jwtSecret, 300);
    config.tokenFondeador   = JwtHelper.generateToken('qa-fondeador', 'FONDEADOR',     config.jwtSecret, 300);
    config.tokenSinAcceso   = JwtHelper.generateToken('qa-soporte',   'SOPORTE',       config.jwtSecret, 300);
    config.tokenExpirado    = JwtHelper.generateExpiredToken('qa-expired', 'ADMINISTRADOR', config.jwtSecret);
    config.tokenSinRolClaim = JwtHelper.generateTokenWithoutRolClaim('qa-norole', config.jwtSecret);

    // ── Headers comunes ───────────────────────────────────────────────────────
    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 30000);
    karate.configure('ssl', true);

    var defaultHeaders = { 'Content-Type': 'application/json', 'Accept': 'application/json' };
    karate.configure('headers', defaultHeaders);

    karate.log('baseUrl:', config.baseUrl);
    karate.log('productionMode:', config.productionMode);

    return config;
}