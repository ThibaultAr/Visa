/* --------------------------------------------------------------------------
Mise en correspondance de points d'interet detectes dans deux images
Copyright (C) 2010, 2011  Universite Lille 1

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------------- */

/* --------------------------------------------------------------------------
Inclure les fichiers d'entete
-------------------------------------------------------------------------- */
#include <stdio.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
using namespace cv;
#include "glue.hpp"
#include "prenom-nom.hpp"

// -----------------------------------------------------------------------
/// \brief Detecte les coins.
///
/// @param mImage: pointeur vers la structure image openCV
/// @param iMaxCorners: nombre maximum de coins detectes
/// @return matrice des coins
// -----------------------------------------------------------------------
Mat iviDetectCorners(const Mat& mImage,
                     int iMaxCorners) {
    // A modifier !
    double tx = mImage.cols, ty = mImage.rows;
    double[3] vec = new double[3];
    cv::goodFeaturesToTrack(mImage, &vec, iMaxCorners, 0.01, 20.)

    tx = vec.x;
    ty = vec.y;
    Mat mCorners = (Mat_<double>(3,4) <<
        .25 * tx, .75 * tx, .25 * tx, .75 * tx,
        .25 * ty, .25 * ty, .75 * ty, .75 * ty,
        1., 1., 1., 1.
        );
    return mCorners;
}

// -----------------------------------------------------------------------
/// \brief Initialise une matrice de produit vectoriel.
///
/// @param v: vecteur colonne (3 coordonnees)
/// @return matrice de produit vectoriel
// -----------------------------------------------------------------------
Mat iviVectorProductMatrix(const Mat& v) {
    double px = v.at<double>(0,0);
    double py = v.at<double>(0,1);
    double pz = v.at<double>(0,2);

    Mat mVectorProduct = Mat::zeros(3,3,CV_64F);
    mVectorProduct.at<double>(0,1) = -pz;
    mVectorProduct.at<double>(0,2) = py;
    mVectorProduct.at<double>(1,0) = pz;
    mVectorProduct.at<double>(1,2) = -px;
    mVectorProduct.at<double>(2,0) = -py;
    mVectorProduct.at<double>(2,1) = px;

    return mVectorProduct;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule la matrice fondamentale.
///
/// @param mLeftIntrinsic: matrice intrinseque de la camera gauche
/// @param mLeftExtrinsic: matrice extrinseque de la camera gauche
/// @param mRightIntrinsic: matrice intrinseque de la camera droite
/// @param mRightExtrinsic: matrice extrinseque de la camera droite
/// @return matrice fondamentale
// -----------------------------------------------------------------------
Mat iviFundamentalMatrix(const Mat& mLeftIntrinsic,
                         const Mat& mLeftExtrinsic,
                         const Mat& mRightIntrinsic,
                         const Mat& mRightExtrinsic) {
    // Doit utiliser la fonction iviVectorProductMatrix
    Mat id34 = Mat::eye(3,4,CV_64F);
    Mat p1 = mLeftIntrinsic * id34 * mLeftExtrinsic;
    Mat p2 = mRightIntrinsic * id34 * mRightExtrinsic;
    Mat p1Inv = p1.inv(DECOMP_SVD);
    Mat o1 = mLeftExtrinsic.inv().col(3);
    Mat mFundamental = iviVectorProductMatrix(p2*o1) * p2 * p1Inv;
    // Retour de la matrice fondamentale
    return mFundamental;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule la matrice des distances entres les
/// points de paires candidates a la correspondance.
///
/// @param mLeftCorners: liste des points 2D image gauche
/// @param mRightCorners: liste des points 2D image droite
/// @param mFundamental: matrice fondamentale
/// @return matrice des distances entre points des paires
// -----------------------------------------------------------------------
Mat iviDistancesMatrix(const Mat& m2DLeftCorners,
                       const Mat& m2DRightCorners,
                       const Mat& mFundamental) {
    // A modifier !
    Mat mDistances = Mat();
    // Retour de la matrice fondamentale
    return mDistances;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule les indices des points homologues.
///
/// @param mDistances: matrice des distances
/// @param fMaxDistance: distance maximale autorisant une association
/// @param mRightHomologous: liste des correspondants des points gauche
/// @param mLeftHomologous: liste des correspondants des points droite
/// @return rien
// -----------------------------------------------------------------------
void iviMarkAssociations(const Mat& mDistances,
                         double dMaxDistance,
                         Mat& mRightHomologous,
                         Mat& mLeftHomologous) {
    // A modifier !
}
