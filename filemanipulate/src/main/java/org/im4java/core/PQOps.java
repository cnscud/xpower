/**************************************************************************
/* This class adds all the PQ methods to an Operation.
/*
/* Copyright (c) 2009 by Bernhard Bablok (mail@bablokb.de)
/*
/* This program is free software; you can redistribute it and/or modify
/* it under the terms of the GNU Library General Public License as published
/* by  the Free Software Foundation; either version 2 of the License or
/* (at your option) any later version.
/*
/* This program is distributed in the hope that it will be useful, but
/* WITHOUT ANY WARRANTY; without even the implied warranty of
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
/* GNU Library General Public License for more details.
/*
/* You should have received a copy of the GNU Library General Public License
/* along with this program; see the file COPYING.LIB.  If not, write to
/* the Free Software Foundation Inc., 59 Temple Place - Suite 330,
/* Boston, MA  02111-1307 USA
/**************************************************************************/

package org.im4java.core;

import org.im4java.core.Operation;

/**
   This class subclasses Operation and adds methods for all commandline
   options of pngquant.

   <p>This class is automatically generated by the source-code generator of
   im4java.</p>

   @version $Revision: 1.14 $
   @author  $Author: bablokb $
*/

public class PQOps extends Operation {

  //////////////////////////////////////////////////////////////////////////////

  /**
     The protected Constructor. You should only use subclasses of PQOps.
  */

  protected PQOps() {
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --force to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps force() {

    iCmdArgs.add("--force");

    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --nofs to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps nofs() {

    iCmdArgs.add("--nofs");

    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --verbose to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps verbose() {

    iCmdArgs.add("--verbose");

    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --skip-if-larger to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps skipIfLarger() {

    iCmdArgs.add("--skip-if-larger");

    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --output to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps output(String pFilename) {

    String       oper;                      // only used in some methods
    StringBuffer buf = new StringBuffer();  // local buffer for option-args
    iCmdArgs.add("--output");

    if (pFilename != null) {
      buf.append(pFilename.toString());
    }
    if (buf.length()>0) {
      iCmdArgs.add(buf.toString());
    }
    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --speed to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps speed(Integer pSpeed) {

    String       oper;                      // only used in some methods
    StringBuffer buf = new StringBuffer();  // local buffer for option-args
    iCmdArgs.add("--speed");

    if (pSpeed != null) {
      buf.append(pSpeed.toString());
    }
    if (buf.length()>0) {
      iCmdArgs.add(buf.toString());
    }
    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --quality to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps quality(Integer pMin, Integer pMax) {

    String       oper;                      // only used in some methods
    StringBuffer buf = new StringBuffer();  // local buffer for option-args
    iCmdArgs.add("--quality");

    if (pMin != null) {
      buf.append(pMin.toString());
    }
    if (pMin != null || pMax != null) {
      buf.append("-");
    }
    if (pMax != null) {
      buf.append(pMax.toString());
    }
    if (buf.length()>0) {
      iCmdArgs.add(buf.toString());
    }
    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --ext to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps ext(String pSuffix) {

    String       oper;                      // only used in some methods
    StringBuffer buf = new StringBuffer();  // local buffer for option-args
    iCmdArgs.add("--ext");

    if (pSuffix != null) {
      buf.append(pSuffix.toString());
    }
    if (buf.length()>0) {
      iCmdArgs.add(buf.toString());
    }
    return this;
  }

  //////////////////////////////////////////////////////////////////////////////

  /**
     Add option --posterize to the pngquant commandline
     (see the documentation of pngquant for details).
  */

  public PQOps posterize(Integer pPrecision) {

    String       oper;                      // only used in some methods
    StringBuffer buf = new StringBuffer();  // local buffer for option-args
    iCmdArgs.add("--posterize");

    if (pPrecision != null) {
      buf.append(pPrecision.toString());
    }
    if (buf.length()>0) {
      iCmdArgs.add(buf.toString());
    }
    return this;
  }


}
