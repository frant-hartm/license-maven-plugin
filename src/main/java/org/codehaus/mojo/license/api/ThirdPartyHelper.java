package org.codehaus.mojo.license.api;

/*
 * #%L
 * License Maven Plugin
 * %%
 * Copyright (C) 2012 CodeLutin, Codehaus, Tony Chemit
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mojo.license.model.LicenseMap;
import org.codehaus.mojo.license.utils.SortedProperties;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Helper class that provides common functionality required by both the mojos and the reports.
 *
 * @author tchemit <chemit@codelutin.com>
 * @since 1.1
 */
public interface ThirdPartyHelper
{

    /**
     * Load all dependencies given the configuration as {@link MavenProject}.
     *
     * @param configuration the configuration of the project and include/exclude to do on his dependencies
     * @return the dictionnary of loaded dependencies as {@link MavenProject} indexed by their gav.
     */
    SortedMap<String, MavenProject> loadDependencies( MavenProjectDependenciesConfigurator configuration );


    /**
     * Try to load maximum of unsafe license mapping using third-party descriptors (from maven repositories) and
     * return it.
     *
     * @param projects           all projects where to read third parties descriptors
     * @param unsafeDependencies all unsafe dependences
     * @param licenseMap         license map where to store new licenses
     * @return the map of loaded missing from the remote missing third party files
     * @throws ThirdPartyToolException if any
     * @throws IOException             if any
     */
    SortedProperties loadThirdPartyDescriptorForUnsafeMapping( SortedSet<MavenProject> unsafeDependencies,
                                                               Collection<MavenProject> projects,
                                                               LicenseMap licenseMap )
        throws ThirdPartyToolException, IOException;

    /**
     * Load unsafe mapping for all dependencies with no license in their pom, we will load the missing file
     * if it exists and alos add all dependencies from licenseMap with no license known.
     *
     * @param licenseMap  the license map of all dependencies.
     * @param missingFile location of an optional missing fille (says where you fix missing license).
     * @return the map of all unsafe mapping
     * @throws IOException if could not load missing file
     */
    SortedProperties loadUnsafeMapping( LicenseMap licenseMap, File missingFile )
        throws IOException;

    /**
     * Creates a license map from given dependencies.
     *
     * @param dependencies dependencies to store in the license map
     * @return the created license map fro the given dependencies
     */
    LicenseMap createLicenseMap( SortedMap<String, MavenProject> dependencies );

    /**
     * Attach the third-party descriptor to the build.
     *
     * @param file location of the third-party descriptor
     */
    void attachThirdPartyDescriptor( File file );

    /**
     * Obtains all dependencies with no license form the given license map.
     *
     * @param licenseMap license map where to find
     * @return all dependencies with no license
     */
    SortedSet<MavenProject> getProjectsWithNoLicense( LicenseMap licenseMap );

    /**
     * Obtains the cache of loaded dependencies indexed by their gav.
     *
     * @return the cache of loaded dependencies indexed by their gav
     */
    SortedMap<String, MavenProject> getArtifactCache();

    /**
     * Loads unsafe mapping and if there is unsafe dependencies try to load them from maven repositories
     * (if flag is on) and returns it.
     *
     * @param licenseMap                license map to read
     * @param missingFile               location of an optional missing file
     * @param useRepositoryMissingFiles flag to use or not third-party descriptor from maven repositories
     * @param unsafeDependencies        all unsafe dependencies
     * @param projectDependencies       all project dependencies
     * @return the loaded unsafe mapping
     * @throws ProjectBuildingException if could not build some dependencies maven project
     * @throws IOException              if could not load missing file
     * @throws ThirdPartyToolException  if pb with third-party tool
     */
    SortedProperties createUnsafeMapping( LicenseMap licenseMap, File missingFile, boolean useRepositoryMissingFiles,
                                          SortedSet<MavenProject> unsafeDependencies,
                                          Collection<MavenProject> projectDependencies )
        throws ProjectBuildingException, IOException, ThirdPartyToolException;

    /**
     * Merges licenses.
     *
     * @param licenseMerges list of license mergeables (each entry is a list of licenses separated by |, the first one
     *                      is the license to use for all the others of the entry).
     * @param licenseMap    license map to merge
     * @throws MojoFailureException if there is a bad license merge definition (says for example two license with
     *                              same name)
     */
    void mergeLicenses( List<String> licenseMerges, LicenseMap licenseMap )
        throws MojoFailureException;
}
