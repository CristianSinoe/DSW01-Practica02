describe('Authentication', () => {
  it('logs in successfully and redirects to /empleadas', () => {
    cy.intercept('GET', '/api/v1/auth/me', {
      statusCode: 200,
      body: {
        id: 'e2e-user-id',
        nombre: 'Ana',
        email: 'ana@empresa.com',
        role: 'ADMIN'
      }
    }).as('authMe');

    cy.visit('/login');

    cy.get('input[formcontrolname="email"]').type('ana@empresa.com');
    cy.get('input[formcontrolname="password"]').type('secreto123');
    cy.get('button[type="submit"]').click();

    cy.wait('@authMe');
    cy.url().should('include', '/empleadas');

    cy.window().then((windowRef) => {
      const rawSession = windowRef.sessionStorage.getItem('auth.session');
      expect(rawSession).to.not.equal(null);
      const session = JSON.parse(rawSession as string) as { email: string; role: string; authHeader: string };
      expect(session.email).to.equal('ana@empresa.com');
      expect(session.role).to.equal('ADMIN');
      expect(session.authHeader).to.contain('Basic ');
    });
  });

  it('shows authentication error when credentials are invalid', () => {
    cy.intercept('GET', '/api/v1/auth/me', {
      statusCode: 401,
      body: {}
    }).as('authMeUnauthorized');

    cy.visit('/login');

    cy.get('input[formcontrolname="email"]').type('ana@empresa.com');
    cy.get('input[formcontrolname="password"]').type('incorrecta123');
    cy.get('button[type="submit"]').click();

    cy.wait('@authMeUnauthorized');
    cy.url().should('include', '/login');
    cy.contains('Credenciales invalidas. Verifica email y contrasena.').should('be.visible');
    cy.window().then((windowRef) => {
      expect(windowRef.sessionStorage.getItem('auth.session')).to.equal(null);
    });
  });
});
