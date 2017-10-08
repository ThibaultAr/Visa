// -----------------------------------------------------------------------
/// \brief Calcule un terme de contrainte a partir d'une homographie.
///
/// \param H: matrice 3*3 définissant l'homographie.
/// \param i: premiere colonne.
/// \param j: deuxieme colonne.
/// \return vecteur definissant le terme de contrainte.
// -----------------------------------------------------------------------
function v = ZhangConstraintTerm(H, i, j)
    v = [
        H(:,i)(1)*H(:,j)(1),
        H(:,i)(1)*H(:,j)(2)+H(:,i)(2)*H(:,j)(1),
        H(:,i)(2)*H(:,j)(2), 
        H(:,i)(3)*H(:,j)(1)+H(:,i)(1)*H(:,j)(3),
        H(:,i)(3)*H(:,j)(2)+H(:,i)(2)*H(:,j)(3), 
        H(:,i)(3)*H(:,j)(3)]'
endfunction

// -----------------------------------------------------------------------
/// \brief Calcule deux equations de contrainte a partir d'une homographie
///
/// \param H: matrice 3*3 définissant l'homographie.
/// \return matrice 2*6 definissant les deux contraintes.
// -----------------------------------------------------------------------
function v = ZhangConstraints(H)
  v = [ZhangConstraintTerm(H, 1, 2); ...
    ZhangConstraintTerm(H, 1, 1) - ZhangConstraintTerm(H, 2, 2)];
endfunction

// -----------------------------------------------------------------------
/// \brief Calcule la matrice des parametres intrinseques.
///
/// \param b: vecteur resultant de l'optimisation de Zhang.
/// \return matrice 3*3 des parametres intrinseques.
// -----------------------------------------------------------------------
function A = IntrinsicMatrix(b)
    B11 = b(1);
    B12 = b(2);
    B13 = b(4);
    B22 = b(3);
    B23 = b(5);
    B33 = b(6);
        
    v0= (B12*B13 - B11*B23)/(B11*B22 - B12*B12);
    lambda= B33 - (B13*B13 + v0*(B12*B13 - B11*B23))/B11;
    alpha= sqrt(lambda/B11);
    bet=sqrt(lambda*B11/(B11*B22 - B12*B12));
    eta=-B12*alpha*alpha*bet/lambda;
    u0=eta*v0/bet - B13*alpha*alpha/lambda;
    
    A = [
        [alpha, eta, u0];
        [0, bet, v0];
        [0, 0, 1]
        ]
endfunction

// -----------------------------------------------------------------------
/// \brief Calcule la matrice des parametres extrinseques.
///
/// \param iA: inverse de la matrice intrinseque.
/// \param H: matrice 3*3 definissant l'homographie.
/// \return matrice 3*4 des parametres extrinseques.
// -----------------------------------------------------------------------
function E = ExtrinsicMatrix(iA, H)
  lambda = 1/abs(iA*H(:,1));
  lambda = lambda(1);
  r1 = lambda * iA * H(:,1);
  r2 = lambda * iA * H(:,2);
  r3 = CrossProduct(r1,r2);
  t = lambda * iA * H(:,3);
  
  E = [r1, r2, r3, t];
endfunction

